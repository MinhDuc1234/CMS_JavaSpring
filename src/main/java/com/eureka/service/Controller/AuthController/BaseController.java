package com.eureka.service.Controller.AuthController;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eureka.service.Controller.ErrorController;
import com.eureka.service.Core.SystemUser;
import com.eureka.service.Core.Request.QuickUpdate;
import com.eureka.service.Core.Request.RequestFilter;
import com.eureka.service.Core.Request.RequestPage;
import com.eureka.service.Core.Response.ResponseData;
import com.eureka.service.Core.Response.ResponseFilter;
import com.eureka.service.Core.Response.ResponsePage;
import com.eureka.service.Core.UI.Input;
import com.eureka.service.Entity.BaseEntity;
import com.eureka.service.Interface.Controller.IController;
import com.eureka.service.Interface.Service.IUserService;
import com.eureka.service.Repository.BaseRepository;
import com.eureka.service.Service.AsyncService;
import com.eureka.service.Service.FileService;
import com.eureka.service.Util.ExcelUtil;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class BaseController<T extends BaseEntity> extends ErrorController implements IController<T> {

    @Value("${server.file-path:./template/}")
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private String templateRoot;

    @Autowired
    protected AsyncService asyncService;

    @Autowired
    protected IUserService userService;

    @Autowired
    protected FileService fileService;

    @Autowired
    protected BaseRepository<T> baseRepository;

    @Override
    public ResponseEntity<ResponseData<ResponsePage<T>>> getAll(RequestPage requestPage) throws Exception {
        SystemUser systemUser = this.userService.getLoggedInUser();
        return ResponseDataEntity(this.baseRepository.getAll(systemUser, requestPage));
    }

    @Override
    public ResponseEntity<ResponseData<T>> getById(String id) throws Exception {
        SystemUser systemUser = this.userService.getLoggedInUser();
        ResponseData<T> responseData = this.baseRepository.findById(id, systemUser);
        return ResponseDataEntity(responseData);
    }

    @Override
    public ResponseEntity<byte[]> excelTemplate() throws IOException {
        InputStream resource = new FileInputStream(
                this.fileService.getDir() + this.baseRepository.getTableName() + ".xlsx");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + this.baseRepository.getTableName() + ".xlsx");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        byte[] bytes = IOUtils.toByteArray(resource);
        return ResponseEntity.ok().headers(headers).contentLength(bytes.length).body(bytes);
    }

    @Override
    public ResponseEntity<ResponseData<Boolean>> quickUpdateField(QuickUpdate quickUpdate) throws Exception {
        SystemUser systemUser = this.userService.getLoggedInUser();
        ResponseData<Boolean> responseData = this.baseRepository.quickUpdateField(quickUpdate, systemUser);
        if (responseData.getStatus() == false)
            this.baseRepository.clear();
        return ResponseDataEntity(responseData);
    }

    @Override
    public ResponseEntity<ResponseData<Boolean>> delete(String id) throws Exception {
        SystemUser systemUser = this.userService.getLoggedInUser();
        ResponseData<Boolean> responseData = this.baseRepository.delete(id, systemUser);
        if (responseData.getStatus() == false)
            this.baseRepository.clear();
        return ResponseDataEntity(responseData);
    }

    @Override
    public ResponseEntity<ResponseData<T>> save(T entity) throws Exception {
        SystemUser systemUser = this.userService.getLoggedInUser();
        ResponseData<T> responseData = null;
        if (entity.isNewItem()) {
            responseData = this.baseRepository.saveWithLineItems(entity, systemUser);
        } else {
            if (this.baseRepository.findById(entity.getId()).getStatus()) {
                responseData = this.baseRepository.updateWithLineItems(entity, systemUser);
            } else {
                responseData = this.baseRepository.saveWithLineItems(entity, systemUser);
            }
        }
        if (responseData.getStatus() == false) {
            this.baseRepository.clear();
        }
        return ResponseDataEntity(responseData);
    }

    @Override
    public ResponseEntity<ResponseData<String>> saveAll(List<T> docs) throws Exception {
        SystemUser systemUser = this.userService.getLoggedInUser();
        this.asyncService.withTransaction(() -> {
            ResponseData<List<T>> responseData = this.baseRepository.saves(docs, systemUser);
            if (responseData.getStatus() == false)
                this.baseRepository.clear();
        });
        return ResponseDataEntity(ResponseData.success("We're processing you data."));
    }

    @Override
    public ResponseEntity<ResponseData<Long>> count(RequestFilter filterQuery) throws Exception {
        SystemUser systemUser = this.userService.getLoggedInUser();
        ResponseData<Long> responseData = this.baseRepository.count(filterQuery, systemUser);
        return ResponseDataEntity(responseData);
    }

    @Override
    public ResponseEntity<ResponseData<List<Input>>> uiConfig() {
        return ResponseDataEntity(ResponseData.success(this.baseRepository.getInputs()));
    }

    @Override
    public ResponseEntity<ResponseData<List<ResponseFilter>>> filter(RequestFilter filterQuery) {
        return ResponseDataEntity(this.baseRepository.filter(filterQuery));
    }

    @Override
    public ResponseEntity<ResponseData<List<ResponseFilter>>> getName(List<String> ids) {
        return ResponseDataEntity(this.baseRepository.getNames(ids));
    }

    @Override
    public ResponseEntity<ResponseData<String>> createWithExcel(MultipartFile file) throws Exception {
        SystemUser systemUser = this.userService.getLoggedInUser();
        final String serverPath = this.fileService.storeFile(file);
        this.asyncService.withTransaction(() -> {
            final String filePath = this.fileService.getFullPath(serverPath);
            List<T> list = ExcelUtil.fromExcel(filePath, this.baseRepository);
            ResponseData<List<T>> responseData = this.baseRepository.saves(list, systemUser, false);
            if (responseData.getStatus() == false)
                this.baseRepository.clear();
        });
        return ResponseDataEntity(
                ResponseData.success("Your file is uploaded. We are processing the file. Please check on queue!"));
    }

    @Override
    public ResponseEntity<ResponseData<List<T>>> getAllLineItems(String id) throws Exception {
        SystemUser systemUser = this.userService.getLoggedInUser();
        RequestPage requestPage = new RequestPage();
        Map<String, List<String>> map = new HashMap<>();
        if (this.getHeaderForeignKey() == null) {
            return ResponseDataEntity(ResponseData.error("This table doesn't support line item"));
        }
        map.put(this.getHeaderForeignKey(), Arrays.asList(id));
        requestPage.setFilterForeignKey(map);
        return ResponseDataEntity(this.baseRepository.fetchAll(systemUser, requestPage));
    }

}