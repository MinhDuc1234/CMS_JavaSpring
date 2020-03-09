package com.eureka.service.Core.Type;

import java.io.Serializable;
import java.sql.Date;

import com.eureka.service.Config.ValueConfig;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeRangeOnDate implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ValueConfig.DATE_FORMAT)
    private Date date;
    private TimeRange timeRange;

    public TimeRangeOnDate() {
    }

    public TimeRangeOnDate(Date date, TimeRange timeRange) {
        this.date = date;
        this.timeRange = timeRange;
    }

}