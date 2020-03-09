package com.eureka.service.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.eureka.service.Core.ExceptionInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ElasticLoggingService {

    @Value("${spring.application.name:BaseService}")
    private String appName;

    @Autowired
    protected ElasticService elasticService;

    @Autowired
    protected AsyncService asyncService;

    @Value("${elasticsearch.index.error:ibayerror}")
    private String index;

    private Queue<ExceptionInfo> queue = new LinkedList<>();

    @Scheduled(fixedDelay = 1000)
    private void sendToES() {
        List<ExceptionInfo> list = this.getList(100);
        if (list.size() > 0) {
            try {
                this.elasticService.bulkIndex(this.index, list);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                this.queue.addAll(list);
                this.addToQueue(e);
            }
        }
    }

    public void addToQueue(Throwable object) {
        this.asyncService.withoutTransaction(() -> {
            ExceptionInfo exceptionInfo = new ExceptionInfo(object, this.appName);
            System.out.println(String.join("\n", exceptionInfo.toMessages()));
            this.queue.add(exceptionInfo);
        });
    }

    private List<ExceptionInfo> getList(Integer size) {
        List<ExceptionInfo> list = new ArrayList<>();
        while (size >= 0) {
            if (this.queue.size() > 0) {
                list.add(this.queue.remove());
            }
            size -= 1;
        }
        return list;
    }

}