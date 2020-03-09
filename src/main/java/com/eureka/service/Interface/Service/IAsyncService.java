package com.eureka.service.Interface.Service;

import com.eureka.service.Interface.Callback.AsyncCallback;

public interface IAsyncService {

    void withTransaction(AsyncCallback callback);

    void withoutTransaction(AsyncCallback callback);

}