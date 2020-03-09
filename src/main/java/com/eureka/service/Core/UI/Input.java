package com.eureka.service.Core.UI;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Input {
    String name;
    String type;
    Boolean multi = false;
    String label = "";
    String hint = "";
    Object param = null;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    Integer size = 6;

    Boolean disable = false;
    Boolean disableUpdate = false;
    Boolean disableCreate = false;
    Boolean canSort = false;
    List<Validate> validates;
}