package com.eureka.service.Converter;

import com.eureka.service.Converter.EnumConverter;
import com.eureka.service.Enum.RoleEnum;

public class RoleEnumConverter extends EnumConverter<String, RoleEnum> {

    public RoleEnumConverter() {
        super(RoleEnum.class);
    }

}
