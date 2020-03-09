package com.eureka.service.Core.Hql;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinClause {
    private String joinTable = "";
    private String joinAs = "";
    private String joinOn = "";

    public JoinClause(String joinTable, String joinAs, String joinOn) {
        this.joinAs = joinAs;
        this.joinTable = joinTable;
        this.joinOn = joinOn;
    }

    public String toClause() {
        return this.joinTable + " " + this.joinAs + " ON " + this.joinOn;
    }
}