package com.eureka.service.Interface.RoleAccess;

import com.eureka.service.Core.Access;
import com.eureka.service.Core.SystemUser;
import com.eureka.service.Interface.Entity.IBaseEntity;
import com.eureka.service.Validator.Setting.IPermission;
import com.eureka.service.Enum.Action;

public interface IRoleAccess {

    Access getReadAccess(IPermission iPerm, Action actionType, SystemUser systemUser);

    Boolean getWriteAccess(IPermission iPerm, Action actionType, SystemUser systemUser, IBaseEntity doc);

    public default Boolean getWriteAccess(IPermission iPerm, Action actionType, SystemUser systemUser) {
        return getWriteAccess(iPerm, actionType, systemUser, null);
    }

}