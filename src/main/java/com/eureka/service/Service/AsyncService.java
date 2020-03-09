package com.eureka.service.Service;

import java.util.Date;

import javax.transaction.Transactional;

import com.eureka.service.Interface.Callback.AsyncCallback;
import com.eureka.service.Interface.Service.IAsyncService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService implements IAsyncService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);

    private void run(AsyncCallback callback) {
        Date date = new Date();
        logger.info("Start executeAsync");
        try {
            callback.run();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("End executeAsync: " + ((new Date()).getTime() - date.getTime()) + " (ms)");
    }

    @Override
    @Async("asyncServiceExecutor")
    @Transactional
    public void withTransaction(AsyncCallback callback) {
        run(callback);
    }

    @Override
    @Async("asyncServiceExecutor")
    public void withoutTransaction(AsyncCallback callback) {
        run(callback);
    }

}