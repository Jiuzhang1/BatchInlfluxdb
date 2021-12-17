package com.sanywind.batchinlfluxdb.service.impl;

import com.sanywind.batchinlfluxdb.util.InfluxDBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Order(value = 1)
public class MyApplicationRunner implements ApplicationRunner {
    @Autowired
    private InfluxDBMapper influxDBMapper;
    @Override
    public void run(ApplicationArguments var1) throws Exception{
        influxDBMapper.queryResult();
        System.out.println("批量执行结束！");
    }
}

