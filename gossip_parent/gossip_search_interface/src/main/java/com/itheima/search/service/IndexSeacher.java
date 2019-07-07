package com.itheima.search.service;

import com.itheima.gossip.pojo.News;
import com.itheima.gossip.pojo.ResultBean;

import java.util.List;

/**
 * 根据前端传递过来的数据查询数据
 */
public interface IndexSeacher {
    public ResultBean queryByKeywords(ResultBean resultBean) throws Exception;
}
