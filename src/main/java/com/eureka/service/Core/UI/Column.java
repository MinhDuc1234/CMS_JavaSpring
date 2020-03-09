package com.eureka.service.Core.UI;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Column {

    @Getter
    @Setter
    @ToString
    public static class MinMaxValue {
        private Integer min;
        private Integer max;
    }

    @Getter
    @ToString
    public static enum ColumnType {
        NUMBER, DECIMAL, DATE, TEXT, SELECT, NONE;
    }

    @Setter(value = AccessLevel.NONE)
    String name;
    ColumnType columnType;
    String fieldName;
    Boolean nullable = true;
    Object param = null;

    private void setName(String name) {
        List<String> arr = new ArrayList<>();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        while (true) {
            Matcher match = Pattern.compile("^[A-Z][a-z]+").matcher(name);
            if (match.find()) {
                arr.add(name.substring(0, match.end()));
                name = name.substring(match.end());
                if (name.length() == 0)
                    break;
            } else {
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                arr.add(name);
                break;
            }
        }
        this.name = arr.stream().collect(Collectors.joining(" "));
    }

    public void setName(String name, Boolean format) {
        if (format) {
            this.setName(name);
        } else {
            this.name = name;
        }
    }
}