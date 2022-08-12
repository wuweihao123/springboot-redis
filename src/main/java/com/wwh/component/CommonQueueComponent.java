package com.wwh.component;

import com.wwh.service.QueueConsumer;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author: wwh
 * @date: 2022/8/12
 * @description:
 */

@Slf4j
@SuppressWarnings("all")
@Component
public class CommonQueueComponent implements ApplicationContextAware {

    /**
     * redission延迟队列名：以应用名（服务名）为队列名
     * 该名称自行定义
     */
    @Value("${spring.application.name}")
    private String queueName;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, QueueConsumer> map = applicationContext.getBeansOfType(QueueConsumer.class);
        map.values().forEach(this::startThread);
    }

    /**
     * 启动线程获取队列，并执行业务
     *
     * @param queueName                队列名
     * @param CommonDelayQueueConsumer 任务回调监听
     * @param <T>                      泛型
     */
    private <T> void startThread(QueueConsumer queueConsumer) {
        RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(queueName);
        //由于此线程需要常驻，所以可以直接新建线程，不需要交给线程池管理
        Thread thread = new Thread(() -> {
            log.info("启动队列名为：{}的监听线程", queueName);
            while (true) {
                try {
                    T t = blockingFairQueue.take();
                    //此处不提供多线程处理，自己决定是否开启多线程(业务中需要开启多线程话，建议使用线程池！！！)
                    queueConsumer.execute(t);
                } catch (Exception e) {
                    log.error("队列监听线程错误,", e);
                }
            }
        });
        thread.setName(queueName);
        thread.start();
    }
}
