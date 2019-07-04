package com.itheima.gossip.mapper;

import com.itheima.gossip.pojo.News;

import java.util.List;

/**
 * 新闻的dao层
 */
public interface NesMapper {
    //查询新闻 用redis记录本次最大id
    public List<News> queryListMaxId(String id);
  //查询当前查询中的的数据最大的id值，将最后的这个id更新到redis中

  //select Max(id) from news where id > 100
  //同步数据到mysql到solr，如果一次性将数据查询过来 内存溢出，还可能出现超时问题
    //一次性不要读太多，比如只读100条

    //查询本次最大id值
    public String queryNextMaxIdByMaxid(String  id);
}
