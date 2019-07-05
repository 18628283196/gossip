package com.itheima.storm;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseBatchBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/**
 * 接收上游KafkaSpout发送过来的日志数据，对数据进行处理，得到用户搜索的数据
 */
public class SplitBolt extends BaseBasicBolt {

    /**
     * 接收上游KafkaSpout发送过来的日志数据，对数据进行处理，得到用户搜索的数据
     * @param tuple
     * @param basicOutputCollector
     */
    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        //接收上游KafkaSpout发送过来的日志数据
        String log = (String) tuple.getValue(4);//获取第五个元素
        if (log != null && log.contains("#CS#")){
            //处理数据 #cs#
            int lastIndexOf = log.lastIndexOf("#CS#");
            String keywords = log.substring(lastIndexOf + 4);
            //将数据发送给下游
            basicOutputCollector.emit(new Values(keywords));
        }


    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("keywords"));

    }
}
