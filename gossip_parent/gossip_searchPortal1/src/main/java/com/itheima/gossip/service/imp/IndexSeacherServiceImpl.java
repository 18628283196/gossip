package com.itheima.gossip.service.imp;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.gossip.pojo.News;
import com.itheima.gossip.pojo.ResultBean;
import com.itheima.gossip.service.IndexSeacherService;
import com.itheima.search.service.IndexSeacher;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class IndexSeacherServiceImpl implements IndexSeacherService {

    @Reference  // 注入服务
    private IndexSeacher indexSeacher;

    @Override
    public ResultBean queryByKeywords(ResultBean resultBean) throws Exception {

        return indexSeacher.queryByKeywords(resultBean);
    }
}
