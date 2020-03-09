package com.eureka.service.Core.Hql;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WhereClause {

    protected String key;
    protected String operator;
    protected Object value;

    public WhereClause(String key, String operator, Object value) {
        this.key = key;
        this.operator = operator;
        this.value = value;
    }

    public String getKey() {
        return this.key.replaceAll("[^a-zA-Z]+", "_");
    }

    public String toClause() {
        if (this.key.contains("."))
            return this.key + " " + this.operator + " :" + this.getKey();
        return "c." + this.key + " " + this.operator + " :" + this.getKey();
    }

}