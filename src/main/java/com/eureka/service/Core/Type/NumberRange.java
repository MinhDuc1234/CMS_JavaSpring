package com.eureka.service.Core.Type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberRange {
    private Integer from;
    private Integer to;

    public Boolean valid(Integer value) {
        if (value >= this.from && value <= this.to)
            return true;
        return false;
    }
}