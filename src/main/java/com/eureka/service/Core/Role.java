package com.eureka.service.Core;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.eureka.service.Config.ValueConfig;
import com.eureka.service.Core.Hql.WhereClause;
import com.eureka.service.Enum.RoleEnum;
import com.eureka.service.Interface.Callback.OwnerCallback;
import com.eureka.service.Interface.Entity.IBaseEntity;
import com.eureka.service.Interface.RoleAccess.IRoleAccess;
import com.eureka.service.Validator.Setting.IPermission;
import com.eureka.service.Enum.Action;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Role implements IRoleAccess {

    private Map<String, Long> map;

    protected Long getPermissionValue(IPermission iPerm) {
        if (this.map == null || iPerm.permissions() == null)
            return 0l;
        return Arrays.asList(iPerm.permissions()).stream().map(t -> {
            if (this.map.containsKey(t))
                return this.map.get(t);
            return 0l;
        }).reduce(0l, (a, b) -> {
            return a | b;
        });
    }

    @Override
    public Access getReadAccess(IPermission iPerm, Action actionType, SystemUser systemUser) {
        Access access = new Access();
        if (actionType.equals(Action.READ)) {
            if (RoleEnum.ROLE_ADMIN.equals(systemUser.getRoleEnum())) {
                ServerQuery serverQuery = new ServerQuery();
                access.setCanAccess(true);
                access.setServerQuery(serverQuery);
                return access;
            }
        }
        Long val = this.getPermissionValue(iPerm);
        if (val != 0) {
            Long fullAction = actionType.getVal() << 1;
            Boolean fullPerm = ((val & fullAction) == fullAction);
            if (fullPerm || (val & actionType.getVal()) == actionType.getVal()) {
                ServerQuery serverQuery = new ServerQuery();
                List<OwnerCallback> ownerCallbacks = ServerQuery.getCallbacks(iPerm.table());
                if (ownerCallbacks != null) {
                    ownerCallbacks
                            .forEach(ownerCallback -> ownerCallback.getReadCallback().update(serverQuery, systemUser));
                }
                access.setServerQuery(serverQuery);
                access.setCanAccess(true);
                if (fullPerm) {
                    return access;
                }
                serverQuery.getWhereClauses()
                        .add(new WhereClause(ValueConfig.ENTITY_CREATED_BY, "=", systemUser.getId()));
                return access;
            }
        }

        return access;
    }

    @Override
    public Boolean getWriteAccess(IPermission iPerm, Action actionType, SystemUser systemUser, IBaseEntity doc) {
        if (RoleEnum.ROLE_ADMIN.equals(systemUser.getRoleEnum())) {
            return true;
        }

        List<OwnerCallback> ownerCallbacks = ServerQuery.getCallbacks(iPerm.table());
        if (ownerCallbacks != null) {
            Boolean flag = false;
            for (OwnerCallback ownerCallback : ownerCallbacks) {
                if (ownerCallback.getWriteCallback().valid(doc, systemUser) == true) {
                    flag = true;
                    break;
                }
            }
            if (flag == false)
                return false;
        }

        Long val = this.getPermissionValue(iPerm);
        if (val != 0) {
            Long fullAction = actionType.getVal() << 1;
            Boolean fullPerm = ((val & fullAction) == fullAction);
            if (fullPerm || (val & actionType.getVal()) == actionType.getVal()) {
                if (fullPerm)
                    return true;
                if (Action.CREATE.equals(actionType)) {
                    return true;
                }
                if (doc != null && systemUser.getId().equals(doc.getCreatedBy())) {
                    return true;
                }
            }
        }
        return false;
    }

}