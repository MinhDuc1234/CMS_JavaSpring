package com.eureka.service.Core;

import java.util.List;

import com.eureka.service.Validator.IFk;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GenericList<T> {

    @IFk
    private List<T> list;

}