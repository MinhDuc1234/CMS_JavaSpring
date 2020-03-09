package com.eureka.service.Interface.Callback;

import com.eureka.service.Entity.BaseEntity;

@FunctionalInterface
public interface UpdateLineItemCallback<T extends BaseEntity> {
	void updateLineItem(T entity);
}