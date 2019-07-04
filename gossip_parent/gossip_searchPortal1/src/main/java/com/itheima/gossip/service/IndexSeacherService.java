package com.itheima.gossip.service;

import com.itheima.gossip.pojo.News;
import com.itheima.gossip.pojo.ResultBean;

import javax.xml.transform.Result;
import java.util.List;

public interface IndexSeacherService {
    public ResultBean queryByKeywords(ResultBean resultBean) throws Exception;
}
