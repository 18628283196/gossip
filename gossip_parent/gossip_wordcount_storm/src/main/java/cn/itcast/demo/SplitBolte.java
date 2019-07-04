package cn.itcast.demo;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.Arrays;
import java.util.Map;

public class SplitBolte extends BaseRichBolt{
    private OutputCollector outputCollector;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }

    /**
     * 处理业务的方法，切割单词，将单词发送给下一层
     * @param tuple
     */
    @Override
    public void execute(Tuple tuple) {
        try {
            Thread.sleep(1000);//睡眠40s
            //接受到数据的同时，调用一个asc方法
            outputCollector .ack(tuple);//认为消息接受到了，发生一个状态码代内存中
            String juzi = tuple.getStringByField("name");
            String[] words = juzi.split(" ");
            for (String word : words) {
                //1是指每次发送一个单词，也可以发送多个单词，也可以不写
                outputCollector.emit(Arrays.asList(null,word, 18));
            }
        } catch (Exception e) {
            //如果没接收到数据，调用fail方法
            outputCollector.fail(tuple);//
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("userId","name","age"));
    }
}
