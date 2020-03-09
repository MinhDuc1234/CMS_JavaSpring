package com.eureka.service.Core.Response;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResponsePage<T> {
    @ApiModelProperty(notes = "Request page number")
    private Integer currentPage = 0;

    @ApiModelProperty(notes = "Total valid record")
    private Long totalRecord;

    @ApiModelProperty(notes = "Request page size")
    private Integer blockSize = 10;

    @ApiModelProperty(notes = "Total valid page")
    private Integer totalPage;

    @ApiModelProperty(notes = "List records of current page")
    private List<T> records;

    public void updateInfo() {
        this.totalPage = (int) Math.ceil(this.totalRecord * 1.0 / this.blockSize);
    }
}