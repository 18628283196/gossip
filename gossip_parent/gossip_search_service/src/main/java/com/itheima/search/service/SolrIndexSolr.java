package com.itheima.search.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.gossip.pojo.News;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Service:发布服务到注册中心
 */
@Service
public class SolrIndexSolr implements  IndexWriter {

    @Autowired
    private CloudSolrServer cloudSolrServer;

    @Override
    public void saveBeans(List<News> newsList) throws Exception {
       cloudSolrServer.addBeans(newsList);
       cloudSolrServer.commit();
    }
}
