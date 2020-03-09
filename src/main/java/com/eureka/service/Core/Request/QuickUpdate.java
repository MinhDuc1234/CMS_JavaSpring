package com.eureka.service.Core.Request;

import com.eureka.service.Validator.IId;
import com.eureka.service.Validator.IRequired;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QuickUpdate {

    @IId
    @IRequired
    private String id;
    @IRequired
    private String fieldName;
    @IRequired
    private Boolean fieldValue;
    
}