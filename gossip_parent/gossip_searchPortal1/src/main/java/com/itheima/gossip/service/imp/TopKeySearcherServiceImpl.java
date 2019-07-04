package com.itheima.gossip.service.imp;

import com.itheima.gossip.service.TopKeySearcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.*;
@Service
public class TopKeySearcherServiceImpl implements TopKeySearcherService {

    @Autowired
    private JedisPool jedisPool;
    @Override
    public List<Map<String, Object>> toKeyFindByNum(Integer num) {
        //1.获取jedis对象
        Jedis jedis = jedisPool.getResource();
        List<Map<String, Object>> list = new ArrayList<>();
        //执行查询获取前几个热搜词：从小到大
        Set<Tuple> tuples = jedis.zrevrangeWithScores("bigData:gossip:topkey", 0, num-1);
        //处理数据
        //热词包含关键词和得分
        for (Tuple tuple : tuples) {
            String topKey = tuple.getElement();//获取热搜词
            double score = tuple.getScore();//获取点击量
           Map<String,Object> map = new HashMap<>();
           map.put("toKey",topKey);
           map.put("score",score);
           list.add(map);
        }
        jedis.close();//释放资源
        return list;
    }
}
