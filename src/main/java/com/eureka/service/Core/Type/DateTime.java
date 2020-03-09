package com.eureka.service.Core.Type;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;

import com.eureka.service.Config.ValueConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import lombok.Getter;
import lombok.Setter;

class SqlTimeDeserializer extends JsonDeserializer<Time> {
    @Override
    public Time deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return Time.valueOf(jp.getValueAsString() + ":00");
    }
}

@Getter
@Setter
public class DateTime {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ValueConfig.DATE_FORMAT)
    private Date date;
    private Time time;

    public DateTime() {
    };

    public DateTime(Date date, Time time) {
        this.date = date;
        this.time = time;
    }
}