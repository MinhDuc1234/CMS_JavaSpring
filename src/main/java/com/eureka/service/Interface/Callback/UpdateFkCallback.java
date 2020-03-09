package com.eureka.service.Interface.Callback;

import java.util.Set;

@FunctionalInterface
public interface UpdateFkCallback {
	void update(Set<String> codes, Object entity);
}