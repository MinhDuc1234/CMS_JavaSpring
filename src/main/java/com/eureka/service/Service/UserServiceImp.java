package com.eureka.service.Service;

import com.eureka.service.Config.ValueConfig;
import com.eureka.service.Core.SystemUser;
import com.eureka.service.Core.UserAuth;
import com.eureka.service.Enum.RoleEnum;
import com.eureka.service.Interface.Service.IUserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;

@Service
public class UserServiceImp implements IUserService {

    @Override
    public SystemUser getLoggedInUser() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserAuth) {
            UserAuth userAuth = (UserAuth) authentication.getPrincipal();
            Claims claims = userAuth.getClaims();
            SystemUser systemUser = new SystemUser();

            if (claims.get(ValueConfig.USER_TYPE) == null) {
                systemUser.setRoleEnum(RoleEnum.ROLE_USER);
            } else {
                systemUser.setRoleEnum(RoleEnum.valueOf(claims.get(ValueConfig.USER_TYPE).toString()));
            }
            systemUser.setUserCode(userAuth.getUsername());
            systemUser.setActTypeCode(claims.get(ValueConfig.ACT_TYPE_CODE).toString());
            systemUser.setCompanyTypeCode(claims.get(ValueConfig.COMPANY_TYPE_CODE).toString());
            systemUser.setUserRoleCode(claims.get(ValueConfig.USER_ROLE_CODE).toString());
            systemUser.setCompanyCode(claims.get(ValueConfig.COMPANY_CODE).toString());

            return systemUser;
        } else {
            throw new UsernameNotFoundException("User is not authenticated; Found " + authentication.getPrincipal()
                    + " of type " + authentication.getPrincipal().getClass() + "; Expected type User");
        }
    }

    @Override
    public String getLoggedInId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetails) {
            String userId = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userId;
        } else {
            throw new UsernameNotFoundException("User is not authenticated; Found " + authentication.getPrincipal()
                    + " of type " + authentication.getPrincipal().getClass() + "; Expected type User");
        }
    }

}
