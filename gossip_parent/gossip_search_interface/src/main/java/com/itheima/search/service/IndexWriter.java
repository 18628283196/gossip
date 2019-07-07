package com.itheima.search.service;

import com.itheima.gossip.pojo.News;

import java.util.List;

/**
 * 门户系统传递过来的新闻数据
 */
public interface IndexWriter {
    //将查询到的数据写入到solr库中
    public void saveBeans(List<News> newsList) throws Exception;
}
