package com.eureka.service.Core;

import com.eureka.service.Interface.Callback.UpdateFkCallback;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FkCode {

    private String fieldName;
    private String validateService;

    private UpdateFkCallback callback;

    public FkCode(String fieldName, UpdateFkCallback callback) {
        this.fieldName = fieldName;
        this.callback = callback;
    }

}