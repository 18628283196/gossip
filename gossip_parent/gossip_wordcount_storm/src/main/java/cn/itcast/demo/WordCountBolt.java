package cn.itcast.demo;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统计单词bolt
 */
public class WordCountBolt extends BaseRichBolt {
    //Map<String,Integer> map = new HashMap<>();//原来的
    //private ConcurrentHashMap<String,Integer> map = new ConcurrentHashMap<>();//第一次改动后：还是存在数据问题
    private static ConcurrentHashMap<String,Integer> map = new ConcurrentHashMap<>();//第二次改动后：没问题了，以后再成员变量上最好 都加上static

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {

    }

    /**
     * 获取上游发送的数据,将结果统计出来
     * @param tuple
     */
    @Override
    public void execute(Tuple tuple) {
        String word = tuple.getStringByField("word");
        Integer num = tuple.getIntegerByField("num");//num为1
        if (map.get(word) != null){//统计过当前的词
            map.put(word,map.get(word)+num);
        }else{//第一次出现
            map.put(word,num);
        }

        System.out.println(map);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
