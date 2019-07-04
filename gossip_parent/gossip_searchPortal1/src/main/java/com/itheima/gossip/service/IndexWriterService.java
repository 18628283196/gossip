package com.itheima.gossip.service;

public interface IndexWriterService {
    //调用数据库，将数据库发送给索引服
    public void saveBean() throws Exception;
}
