package com.eureka.service.Core.Type;

import java.sql.Date;

import com.eureka.service.Config.ValueConfig;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DateRange {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ValueConfig.DATE_FORMAT)
    private Date from;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ValueConfig.DATE_FORMAT)
    private Date to;

    public DateRange() {
    }

    public DateRange(Date from, Date to) {
        this.from = from;
        this.to = to;
    }
}