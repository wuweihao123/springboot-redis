package com.wwh.controller;

import com.wwh.component.CommonQueueComponent;
import com.wwh.config.QueueProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author: wwh
 * @date: 2022/8/12
 * @description:
 */
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private QueueProducer queueProducer;

    @PostMapping("/test")
    public void test() {
        queueProducer.addQueue("dads", 1, TimeUnit.MINUTES, "test");
    }
}
