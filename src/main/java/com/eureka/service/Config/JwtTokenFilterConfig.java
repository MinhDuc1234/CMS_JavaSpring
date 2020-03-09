package com.eureka.service.Config;

import com.eureka.service.Core.SystemUser;
import com.eureka.service.Enum.RoleEnum;
import com.eureka.service.Security.JwtTokenFilter;
import com.eureka.service.Security.JwtTokenProvider;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtTokenFilterConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilterConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        System.out.println("################ " + this.jwtTokenProvider.createToken(new SystemUser() {
            private static final long serialVersionUID = 1L;
            {
                setUserCode("0");
                setRoleEnum(RoleEnum.ROLE_ADMIN);
                setCompanyCode("0");
                setActTypeCode("0");
                setCompanyTypeCode("0");
                setUserRoleCode("0");
            }
        }));
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        JwtTokenFilter customFilter = new JwtTokenFilter(jwtTokenProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
