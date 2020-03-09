package com.eureka.service.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eureka.service.Config.ValueConfig;
import com.eureka.service.Core.Hql.JoinClause;
import com.eureka.service.Core.Hql.WhereClause;
import com.eureka.service.Core.Request.Sort;
import com.eureka.service.Interface.Callback.OwnerCallback;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ServerQuery {

    public static Map<String, List<OwnerCallback>> validateMap = new HashMap<>();

    public static void register(String keyName, OwnerCallback checkOwner) {
        if (!ServerQuery.validateMap.containsKey(keyName))
            ServerQuery.validateMap.put(keyName, new ArrayList<>());
        ServerQuery.validateMap.get(keyName).add(checkOwner);
    }

    public static List<OwnerCallback> getCallbacks(String keyName) {
        if (ServerQuery.validateMap.containsKey(keyName)) {
            return ServerQuery.validateMap.get(keyName);
        }
        return null;
    }

    private String select = "c";
    List<WhereClause> whereClauses;
    List<List<WhereClause>> whereOrClauses;

    Integer skip = 0;

    @Setter(value = AccessLevel.NONE)
    Integer limit = 10;

    public void setLimit(Integer limit) {
        if (limit > 100) {
            this.limit = 100;
        } else {
            this.limit = limit;
        }
    }
    
    protected List<Sort> orders;
    
    List<JoinClause> joins = new ArrayList<>();

    public ServerQuery() {
        this.whereClauses = new ArrayList<>();
        this.whereOrClauses = new ArrayList<>();
        this.whereClauses.add(new WhereClause(ValueConfig.ENTITY_IS_DELETE, "=", false));
    }

    public void addWhereClauses(List<WhereClause> whereClauses) {
        this.whereClauses.addAll(whereClauses);
    }

}