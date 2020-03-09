package com.eureka.service.Interface.Entity;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

/**
 * IUserAuth
 */
public interface IUserAuth extends UserDetails {
    Claims getClaims();
}