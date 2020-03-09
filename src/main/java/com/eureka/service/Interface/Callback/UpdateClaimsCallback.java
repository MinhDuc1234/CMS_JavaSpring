package com.eureka.service.Interface.Callback;

import io.jsonwebtoken.Claims;

@FunctionalInterface
public interface UpdateClaimsCallback {
	void update(Claims claims);
}