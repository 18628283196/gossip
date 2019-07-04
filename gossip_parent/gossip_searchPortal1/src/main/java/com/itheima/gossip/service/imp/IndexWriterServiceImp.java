package com.itheima.gossip.service.imp;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.gossip.mapper.NesMapper;
import com.itheima.gossip.pojo.News;
import com.itheima.gossip.service.IndexWriterService;
import com.itheima.search.service.IndexWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
//选择普通的service
@Service
public class IndexWriterServiceImp implements IndexWriterService {
    /**
     * 查询数据库的数据，将数据发送给索引服
     */
    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private NesMapper nesMapper;

    @Reference(timeout = 5000)
    private IndexWriter indexWriter;
    @Override
    public void saveBean() throws Exception {
        /**
         * 从数据库中查询最大id
         * 2.使用mapper（maxId） 100
         * 查询当前的最大id，方便下次查询
         * 3.调用索引服，将100条数据传递给索引服务
         */
        Jedis jedis =jedisPool.getResource();
        //得到上次的最大id值
        String id = jedis.get("bigdata:gossip:maxId");
        jedis.close();
        if (id == null){
            id = "0";
        }

        while (true){
            //只查询100条
            System.out.println("最大id="+id);
            List<News> newsList = nesMapper.queryListMaxId(id);
            if (newsList==null || newsList.size()<=0){
                System.out.println("没有增量数据了");
                jedis = jedisPool.getResource();
                //最大id存入redis中
                jedis.set("bigdata:gossip:maxId",id);

                break;
            }
            System.out.println("当前查询到:"+newsList.size());

            //3. 调用索引写入服务，将新闻数据写入索引库
            SimpleDateFormat formatOld = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat formatNew = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            for (News news : newsList) {
                //从数据库中获取的日期：2019-01-14 09:52:53
                String timeOld = news.getTime();
                //转成日期
                Date oldDate = formatOld.parse(timeOld);
                //格式化成solr需要的格式
                String timeNew = formatNew.format(oldDate);
                news.setTime(timeNew);

            }

            //将当前查询到的数据保存到solr中
            indexWriter.saveBeans(newsList);
            //查询本次查询中最大的id，下次循环使用
            id = nesMapper.queryNextMaxIdByMaxid(id);
        }
    }
}
