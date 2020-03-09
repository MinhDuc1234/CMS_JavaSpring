package com.eureka.service.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;

@Service
public class FileService {

    @Value("${server.file-path:./template/}")
    @Getter
    private String dir;

    public String storeFile(MultipartFile file) throws IOException {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        String fileDir = dir.toString() + (calendar.get(Calendar.YEAR)) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/"
                + calendar.get(Calendar.DAY_OF_MONTH);
        Path filePath = Paths
                .get(fileDir + "/" + date.getTime() + "." + FilenameUtils.getExtension(file.getOriginalFilename()));

        File _dir = new File(fileDir);
        if (!_dir.exists())
            _dir.mkdirs();

        file.transferTo(filePath.toFile());
        return filePath.toString().substring(this.dir.length());
    }

    public InputStreamResource downloadFile(String filePath) throws IOException {
        String _filePath = this.dir + filePath;
        InputStreamResource resource = new InputStreamResource(new FileInputStream(_filePath));
        return resource;
    }

    public String getFullPath(String filePath) {
        return this.dir + filePath;
    }
}
