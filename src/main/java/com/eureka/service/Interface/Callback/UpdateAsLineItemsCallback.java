package com.eureka.service.Interface.Callback;

import com.eureka.service.Core.SystemUser;
import com.eureka.service.Core.Response.ResponseData;
import com.eureka.service.Entity.BaseEntity;

@FunctionalInterface
public interface UpdateAsLineItemsCallback<T extends BaseEntity> {
	ResponseData<Boolean> updateAsLineItems(T entity, SystemUser systemUser) throws Exception;
}