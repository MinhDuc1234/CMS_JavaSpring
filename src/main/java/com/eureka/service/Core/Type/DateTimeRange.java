package com.eureka.service.Core.Type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DateTimeRange {
    private DateTime from = new DateTime();
    private DateTime to = new DateTime();
}