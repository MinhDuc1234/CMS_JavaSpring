package com.eureka.service.Core;

import com.eureka.service.Validator.IPassword;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Password {
    @IPassword
    private String password;
    @IPassword
    private String rePassword;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public boolean isValid() {
        return this.password.equals(this.rePassword);
    }
}