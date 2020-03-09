package com.eureka.service.Controller;

import java.util.List;
import java.util.stream.Collectors;

import com.eureka.service.Core.Response.ResponseData;
import com.eureka.service.Exception.UserAuthException;
import com.eureka.service.Service.ElasticLoggingService;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ErrorController {

    @Autowired
    protected ElasticLoggingService elasticLoggingService;

    public static <T> ResponseEntity<ResponseData<T>> ResponseDataEntity(final ResponseData<T> responseData,
            final HttpStatus status) {
        return new ResponseEntity<>(responseData, status);
    }

    protected static <T> ResponseEntity<ResponseData<T>> ResponseDataEntity(final ResponseData<T> responseData) {
        return ResponseDataEntity(responseData, responseData.getStatusCode());
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<ResponseData<String>> handleException(final Exception ex) {
        this.elasticLoggingService.addToQueue(ex);
        return ResponseDataEntity(ResponseData.error(ex.getMessage()));
    }

    @ExceptionHandler({ SQLServerException.class })
    public ResponseEntity<ResponseData<String>> handleException(final SQLServerException ex) {
        this.elasticLoggingService.addToQueue(ex);
        return ResponseDataEntity(ResponseData.error("The error from SQL Server: \"" + ex.getMessage() + "\""));
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<ResponseData<String>> handleException(ConstraintViolationException ex) {
        this.elasticLoggingService.addToQueue(ex);
        return ResponseDataEntity(ResponseData
                .error("Please check values are required and the foreign fields. The error from SQL Server: \""
                        + ex.getSQLException().getMessage() + "\""));
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<ResponseData<List<String>>> handleException(final MethodArgumentNotValidException ex) {
        this.elasticLoggingService.addToQueue(ex);

        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(t -> {
            return t.getField() + ": " + t.getDefaultMessage();
        }).collect(Collectors.toList());

        ObjectError objectError = ex.getBindingResult().getGlobalError();
        if (objectError != null) {
            errors.add(objectError.getObjectName() + ": " + objectError.getDefaultMessage());
        }

        return ResponseDataEntity(ResponseData.error(errors));
    }

    @ExceptionHandler({ UserAuthException.class })
    public ResponseEntity<ResponseData<String>> handleException(final UserAuthException ex) {
        this.elasticLoggingService.addToQueue(ex);
        return ResponseDataEntity(ResponseData.error(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    public ResponseEntity<ResponseData<String>> handleException(final HttpRequestMethodNotSupportedException ex) {
        this.elasticLoggingService.addToQueue(ex);
        return ResponseDataEntity(new ResponseData<>(ex.getMessage()), HttpStatus.METHOD_NOT_ALLOWED);
    }

}