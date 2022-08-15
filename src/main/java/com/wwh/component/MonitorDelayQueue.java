package com.wwh.component;

import com.wwh.service.QueueConsumer;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class MonitorDelayQueue {

    @Value("${spring.application.name}")
    private String queueName;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private Comsumer comsumer;

    @PostConstruct
    protected void init() {
        startThread(comsumer);
    }

    private <T> void startThread(QueueConsumer queueConsumer) {
        Executor executor = Executors.newCachedThreadPool();
        RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(queueName);
        //由于此线程需要常驻，所以可以直接新建线程，不需要交给线程池管理
        Thread thread = new Thread(() -> {
            log.info("启动队列名为：{}的监听线程", queueName);
            while (true) {
                try {
                    T poll = blockingFairQueue.poll();
                    T t = blockingFairQueue.take();
                    //此处不提供多线程处理，自己决定是否开启多线程(业务中需要开启多线程话，建议使用线程池！！！)
                    executor.execute(()->{
                        try {
                            queueConsumer.execute(t);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    log.error("队列监听线程错误,", e);
                }
            }
        });
        thread.setName(queueName);
        thread.start();
    }
}
