package com.itheima.storm;

import org.apache.storm.shade.org.eclipse.jetty.util.ajax.JSON;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordCountBolt extends BaseBasicBolt {
    private HashMap<String,Integer> map = new HashMap<>();

    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        //获取上游数据
        String keywords = tuple.getStringByField("keywords");
        //统计次数
        if (map.get(keywords) != null && map.get(keywords)>0){
            map.put(keywords,map.get(keywords)+1);
        }else {
            map.put(keywords,1);
        }
        //发送数据给下游，这个位置不能直接将map数据发走，下游kafkabolt只负责发送数据
        //处理为最终数据 topic：keywords "[{'topkeywords':'','score':99},{}]"
         List<Map<String,Object>> resultList = new ArrayList<>();
        //将map中的数据封装到List中,map.keySet()返回的是所有的键
        String json="";
        for (String key:map.keySet()){
            Integer score = map.get(key);//key：搜索的内容 keywords  score：次数
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("keywords",key);
            resultMap.put("score",score);
            resultList.add(resultMap);

            //将list变成json字符串发送走
             json = (String) com.alibaba.fastjson.JSON.toJSONString(resultList);
            basicOutputCollector.emit(new Values(json));

        }
        System.out.println(json);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        //名字是固定的
        outputFieldsDeclarer.declare(new Fields("message"));
    }
}
