package com.itheima.gossip.timing;

import com.itheima.gossip.service.IndexWriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 定时器，让service的方法定时的执行
 */
@Component
public class NewsTiming {
    //注入servic
    @Autowired
    private IndexWriterService indexWriterService;

    //@Scheduled(cron = "0 0/30  * ? * *")
    public void save() {
        System.out.println(new Date().toLocaleString());
        try {
            indexWriterService.saveBean();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
