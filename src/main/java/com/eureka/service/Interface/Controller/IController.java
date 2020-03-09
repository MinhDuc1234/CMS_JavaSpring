package com.eureka.service.Interface.Controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import com.eureka.service.Core.Request.QuickUpdate;
import com.eureka.service.Core.Request.RequestFilter;
import com.eureka.service.Core.Request.RequestPage;
import com.eureka.service.Core.Response.ResponseData;
import com.eureka.service.Core.Response.ResponseFilter;
import com.eureka.service.Core.Response.ResponsePage;
import com.eureka.service.Core.UI.Input;
import com.eureka.service.Validator.IFileType;
import com.eureka.service.Validator.IFk;
import com.eureka.service.Validator.IId;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;

@Validated
public interface IController<T> {

	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
	@ApiOperation(value = "Get all records by page number")
	public ResponseEntity<ResponseData<ResponsePage<T>>> getAll(@Valid @RequestBody RequestPage requestPage)
			throws Exception;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(value = "Get record by record's ID")
	public ResponseEntity<ResponseData<T>> getById(@Valid @PathVariable @IId String id) throws Exception;

	@Transactional
	@RequestMapping(value = "/quick-update-field", method = RequestMethod.POST, produces = "application/json")
	@ApiOperation(value = "Update status(true/false) fields record by record's ID")
	public ResponseEntity<ResponseData<Boolean>> quickUpdateField(@Valid @RequestBody QuickUpdate quickUpdate)
			throws Exception;

	@Transactional
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
	@ApiOperation(value = "Delete record by record's ID")
	public ResponseEntity<ResponseData<Boolean>> delete(@Valid @PathVariable @IId String id) throws Exception;

	@Transactional
	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
	@ApiOperation(value = "Create new or update record")
	public ResponseEntity<ResponseData<T>> save(@RequestBody @IFk T doc) throws Exception;

	@Transactional
	@RequestMapping(value = "/save-all", method = RequestMethod.POST, produces = "application/json")
	@ApiOperation(value = "Save multiple records")
	public ResponseEntity<ResponseData<String>> saveAll(@Valid @IFk @RequestBody List<T> docs) throws Exception;

	@RequestMapping(value = "/count", method = RequestMethod.POST, produces = "application/json")
	@ApiOperation(value = "Count total record")
	public ResponseEntity<ResponseData<Long>> count(@Valid @RequestBody RequestFilter requestFilter) throws Exception;

	@RequestMapping(value = "/ui-config", method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(value = "Get fields decription")
	public ResponseEntity<ResponseData<List<Input>>> uiConfig();

	@RequestMapping(value = "/filter", method = RequestMethod.POST, produces = "application/json")
	@ApiOperation(value = "RequestPage record by parameter")
	public ResponseEntity<ResponseData<List<ResponseFilter>>> filter(@Valid @RequestBody RequestFilter requestFilter);

	@RequestMapping(value = "/names/{ids}", method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(value = "Get records name by list IDs")
	public ResponseEntity<ResponseData<List<ResponseFilter>>> getName(@Valid @PathVariable @IId List<String> id);

	@RequestMapping(value = "excel-template", method = RequestMethod.GET)
	@ApiOperation(value = "Download excel file to input data manual with the excel file")
	public ResponseEntity<byte[]> excelTemplate() throws IOException;

	@RequestMapping(value = "excel", method = RequestMethod.POST, produces = "application/json")
	@ApiOperation(value = "Create with excel file")
	public ResponseEntity<ResponseData<String>> createWithExcel(
			@Valid @RequestParam("file") @IFileType(param = {
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" }) MultipartFile file)
			throws Exception;

	@RequestMapping(value = "/get-all-line-items/{id}", method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(value = "Get all line items")
	public ResponseEntity<ResponseData<List<T>>> getAllLineItems(@Valid @PathVariable @IId String id) throws Exception;
	
	public default String getHeaderForeignKey() {
		return null;
	}

}