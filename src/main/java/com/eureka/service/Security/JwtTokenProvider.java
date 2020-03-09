package com.eureka.service.Security;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.eureka.service.Config.ValueConfig;
import com.eureka.service.Core.SystemUser;
import com.eureka.service.Core.UserAuth;
import com.eureka.service.Enum.RoleEnum;
import com.eureka.service.Exception.CustomException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {
    @Value("${security.jwt.secret:JwtSecretKey}")
    private String secretKey;

    @Value("${security.jwt.expiration:#{24*60*60*1000}}")
    private long validityInMilliseconds = 3600000;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(SystemUser systemUser) {
        Claims claims = Jwts.claims().setSubject(systemUser.getId());

        if (systemUser.getUpdateClaimsCallbacks() != null) {
            systemUser.getUpdateClaimsCallbacks().forEach(t -> {
                t.update(claims);
            });
        }

        claims.put(ValueConfig.USER_TYPE, systemUser.getRoleEnum());
        claims.put(ValueConfig.ACT_TYPE_CODE, systemUser.getActTypeCode());
        claims.put(ValueConfig.COMPANY_TYPE_CODE, systemUser.getCompanyTypeCode());
        claims.put(ValueConfig.USER_ROLE_CODE, systemUser.getUserRoleCode());
        claims.put(ValueConfig.COMPANY_CODE, systemUser.getCompanyCode());

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        String role = claims.get(ValueConfig.USER_TYPE) == null ? RoleEnum.ROLE_USER.getAuthority()
                : (String) claims.get(ValueConfig.USER_TYPE);

        UserAuth userAuth = new UserAuth(claims.getSubject(), "", true, true, true, true,
                Arrays.asList(RoleEnum.valueOf(role)));
        userAuth.setClaims(claims);
        return new UsernamePasswordAuthenticationToken(userAuth, "", userAuth.getAuthorities());
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}