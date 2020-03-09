package com.eureka.service.Core.Request;

import com.eureka.service.Validator.IValueRange;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Sort {
    private String active;
    @IValueRange(params = { "desc", "asc" })
    private String direction;
}