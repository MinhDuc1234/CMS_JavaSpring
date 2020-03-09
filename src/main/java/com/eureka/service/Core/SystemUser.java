package com.eureka.service.Core;

import java.util.List;

import com.eureka.service.Entity.UserEntity;
import com.eureka.service.Interface.Callback.UpdateClaimsCallback;

import lombok.Getter;
import lombok.Setter;

/**
 * SystemUser
 */
@Getter
@Setter
public class SystemUser extends UserEntity {

    private static final long serialVersionUID = 1L;

    private String companyCode;
    private String companyTypeCode;
    private String actTypeCode;
    private String userRoleCode;

    List<UpdateClaimsCallback> updateClaimsCallbacks;

    @Override
    public String getDisplay() {
        return null;
    }

}