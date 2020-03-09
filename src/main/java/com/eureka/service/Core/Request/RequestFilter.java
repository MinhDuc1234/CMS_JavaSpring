package com.eureka.service.Core.Request;

import java.util.List;
import java.util.Map;

import com.eureka.service.Validator.IEnum;
import com.eureka.service.Validator.IId;
import com.eureka.service.Validator.IMaxLength;
import com.eureka.service.Validator.IRequired;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestFilter {

    @IMaxLength(param = 100)
    @IRequired
    @ApiModelProperty(example = " ")
    protected String queryString;

    @ApiModelProperty(hidden = true)
    protected Boolean fetchAll = false;

    @IEnum
    @ApiModelProperty(notes = "Please ignore this field")
    protected Map<String, List<String>> filterEnum;

    @IId
    @ApiModelProperty(notes = "Please ignore this field")
    protected Map<String, List<String>> filterForeignKey;

    @ApiModelProperty(notes = "Please ignore this field")
    protected Map<String, Boolean> filterCheckbox;

    @IId
    @ApiModelProperty(notes = "List IDs we don't want to include on the result")
    protected List<String> excludeIds;
    
    protected List<Sort> orders;

    public RequestFilter() {
        this.queryString = "";
    }

    public RequestFilter(String queryString) {
        this.queryString = queryString;
    }

}