package com.eureka.service.Core;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FkError {

    private String fieldName;
    private Set<String> codes;

}