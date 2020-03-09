package com.eureka.service.Interface.Callback;

import com.eureka.service.Core.SystemUser;
import com.eureka.service.Core.ServerQuery;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OwnerCallback
 */
@Getter
@AllArgsConstructor
public class OwnerCallback {

    @FunctionalInterface
    public interface ReadCallback {
        void update(ServerQuery serverQuery, SystemUser systemUser);
    }

    @FunctionalInterface
    public interface WriteCallback {
        Boolean valid(Object data, SystemUser systemUser);
    }

    ReadCallback readCallback;
    WriteCallback writeCallback;

}