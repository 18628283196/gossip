package com.itheima.search.listener;

import com.google.gson.Gson;
import com.itheima.gossip.pojo.News;
import com.itheima.search.service.SolrIndexSolr;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * 自定义监听类
 */
@Component
public class SpiderKafkaConsumer implements MessageListener<Integer,String> {
    @Autowired
    private SolrIndexSolr solrIndexSolr;
    private Gson gson = new Gson();
    @Override
    public void onMessage(ConsumerRecord<Integer, String> data) {
        try {
            //得到新闻数据
            String newsString = data.value();
            News news = gson.fromJson(newsString, News.class);
            //保存到数据库之前，需要对日期数据进行处理
            SimpleDateFormat formatOld = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat formatNew = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                //从数据库中获取的日期：2019-01-14 09:52:53
                String timeOld = news.getTime();
                //转成日期
                Date oldDate = formatOld.parse(timeOld);
                //格式化成solr需要的格式
                String timeNew = formatNew.format(oldDate);
                news.setTime(timeNew);

                //调用索引写入方法，将新闻数据保存到solr中
            solrIndexSolr.saveBeans(Arrays.asList(news));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
