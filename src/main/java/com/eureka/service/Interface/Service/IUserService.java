package com.eureka.service.Interface.Service;

import javax.servlet.http.HttpServletRequest;

import com.eureka.service.Core.SystemUser;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public interface IUserService {
    SystemUser getLoggedInUser() throws Exception;

    String getLoggedInId();

    public default String getJwtToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        return request.getHeader("Authorization");
    }
}
