package com.eureka.service.Enum;

import com.eureka.service.Interface.Enum.IEnum;

import org.springframework.security.core.GrantedAuthority;

public enum RoleEnum implements GrantedAuthority, IEnum<String> {
    ROLE_ADMIN("ROLE_ADMIN"), ROLE_USER("ROLE_USER");
    
    private String value;
    RoleEnum(String value) {
        this.value = value;
    }

    public String getAuthority() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }

    public static RoleEnum parseValue(String value) {
        return IEnum.parseValue(value, RoleEnum.class);
    }

}