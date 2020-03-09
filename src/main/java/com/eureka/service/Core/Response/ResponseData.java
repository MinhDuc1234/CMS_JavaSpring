package com.eureka.service.Core.Response;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData<T> {

    public static <T> ResponseData<T> error(List<String> errors, HttpStatus statusCode) {
        ResponseData<T> responseData = new ResponseData<T>();
        responseData.setData(null);
        responseData.setMessages(errors);
        responseData.setMessage(String.join("\n", errors));
        responseData.setStatusCode(statusCode);
        return responseData;
    }

    public static <T> ResponseData<T> error(List<String> errors) {
        return error(errors, HttpStatus.BAD_REQUEST);
    }

    public static <T> ResponseData<T> error(String error, HttpStatus statusCode) {
        ResponseData<T> responseData = new ResponseData<T>();
        responseData.setData(null);
        responseData.getMessages().add(error);
        responseData.setMessage(error);
        responseData.setStatusCode(statusCode);
        return responseData;
    }

    public static <T> ResponseData<T> error(String error) {
        return error(error, HttpStatus.BAD_REQUEST);
    }

    public static <T> ResponseData<T> success(T data) {
        ResponseData<T> responseData = new ResponseData<T>();
        responseData.setData(data);
        responseData.setMessage("");
        responseData.setStatusCode(HttpStatus.OK);
        return responseData;
    }

    public static <T> ResponseData<T> forbidden() {
        return forbidden("You dont have permission");
    }

    public static <T> ResponseData<T> forbidden(String msg) {
        return error(msg, HttpStatus.FORBIDDEN);
    }

    public static <T> ResponseData<T> unauthorized() {
        return error("You dont have permission", HttpStatus.UNAUTHORIZED);
    }

    public static <T> ResponseData<T> notFound() {
        return notFound("Not found");
    }

    public static <T> ResponseData<T> notFound(String msg) {
        return error(msg, HttpStatus.NOT_FOUND);
    }

    public static <T> ResponseData<T> from(ResponseData<?> respData) {
        ResponseData<T> responseData = new ResponseData<T>();
        responseData.setData(null);
        responseData.getMessages().addAll(respData.getMessages());
        responseData.setMessage(respData.getMessage());
        responseData.setStatusCode(respData.getStatusCode());
        return responseData;
    }

    private T data = null;
    private String message = "";
    private List<String> messages = new ArrayList<>();
    @ApiModelProperty(hidden = true)
    private HttpStatus statusCode = HttpStatus.OK;

    public Boolean getStatus() {
        return this.statusCode != null ? this.statusCode.is2xxSuccessful() : false;
    }

    public ResponseData(T data) {
        this.data = data;
        this.statusCode = HttpStatus.OK;
        this.message = "";
    }

}