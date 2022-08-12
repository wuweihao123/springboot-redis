package com.wwh.component;

import com.wwh.service.QueueConsumer;
import org.springframework.stereotype.Component;

/**
 * @author: wwh
 * @date: 2022/8/12
 * @description:
 */
@Component
public class Comsumer implements QueueConsumer {

    @Override
    public void execute(Object o) {
        System.out.println(o);
    }
}
