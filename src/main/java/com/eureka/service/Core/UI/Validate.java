package com.eureka.service.Core.UI;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Validate {
    String message;
    Object param;
    @ApiModelProperty(notes = "Validator name")
    String name;
}