package com.lvmoney.cloud.customer.controller;

import com.lvmoney.cloud.customer.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 这种写法在zipkin中追踪不到
 */
@RestController
public class ConsumerController {

    @Autowired
    private ConsumerService consumerService;

    @GetMapping("/consumer")
    public String getProducer() {

        return consumerService.consumer();
    }
}
