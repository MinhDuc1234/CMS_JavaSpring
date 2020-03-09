package com.eureka.service.Core.Type;

import java.io.Serializable;
import java.sql.Time;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeRange implements Serializable {
    private static final long serialVersionUID = 1L;

    private Time from;
    private Time to;

    public TimeRange() {
    }

    public TimeRange(Time from, Time to) {
        this.from = from;
        this.to = to;
    }
}