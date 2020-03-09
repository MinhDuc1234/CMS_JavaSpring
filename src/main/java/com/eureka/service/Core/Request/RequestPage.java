package com.eureka.service.Core.Request;

import com.eureka.service.Validator.IMax;
import com.eureka.service.Validator.IMin;
import com.eureka.service.Validator.IRequired;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestPage extends RequestFilter {

    @IRequired
    @ApiModelProperty(required = true, notes = "The min page number is 1", example = "1")
    @IMin(param = 1)
    private Integer pageNumber;

    @IRequired
    @ApiModelProperty(required = true, notes = "5 <= Number record <= 100", example = "10")
    @IMin(param = 5)
    @IMax(param = 100)
    private Integer numberRecord;

    public RequestPage() {
        super();
        this.pageNumber = 1;
        this.numberRecord = 5;
    }

    public RequestPage(Integer pageNumber, Integer numberRecord, String queryString) {
        super(queryString);
        this.pageNumber = pageNumber;
        this.numberRecord = numberRecord;
    }
}