package com.box.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/9/12 0012.
 */
@Component
public class Jobdemo {
    @Scheduled(cron="0 0 12 * * ?")
    public void  demotest(){

        System.out.println("定时器");
    }
}
