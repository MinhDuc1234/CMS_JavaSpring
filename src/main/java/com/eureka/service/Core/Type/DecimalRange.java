package com.eureka.service.Core.Type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecimalRange {
    private Double from;
    private Double to;

    public Boolean valid(Double value) {
        if (value >= this.from && value <= this.to)
            return true;
        return false;
    }
}