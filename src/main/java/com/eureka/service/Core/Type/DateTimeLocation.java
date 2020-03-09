package com.eureka.service.Core.Type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DateTimeLocation {
    private DateTime dateTime;
    private Location location;

    public DateTimeLocation() {
    }

    public DateTimeLocation(DateTime dateTime, Location location) {
        this.dateTime = dateTime;
        this.location = location;
    }
}