package cn.itcast.demo;


import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;

import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;


import java.util.Map;
import java.util.Random;

public class ReadFileSpout extends BaseRichSpout {
    private SpoutOutputCollector spoutOutputCollector;
    private Random random;
    /**
     * @open 连接或打开数据源，类似于构造方法，只在当前类被创建的时候初始化一次
     * @param conf  storm集群或用户配置对象，一般不在这配置
     * @param topologyContext topology的上下文，一般也不用
     * @param spoutOutputCollector 收集器，收集数据的   可以将数据读取到collector，由collector将数据发送到storm框架
     */
    @Override
    public void open(Map conf, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.spoutOutputCollector = spoutOutputCollector;
        this.random = new Random();

    }

    /**
     * nextTuple 底层是一个死循环，读取数据源中的数据，主动的将数据向下游发送
     */
    @Override
    public void nextTuple() {
        String[] sentences = new String[]{"张三","李四","王五"};
        int index = random.nextInt(sentences.length);
        try {
            //一行一行的读取数据
            String line = sentences[index];
            Thread.sleep(1000);
            if (line != null && line.length()>0){
                //将读取一行数据发走
                spoutOutputCollector.emit(new Values(line),line);//开启ack方法，后面必须加一个参数，什么都行
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param outputFieldsDeclarer
     *      声明输出字段
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("name"));
    }

    //是一个让下游调用的方法，如果接受到了数据，下游就可调用
    @Override
    public void ack(Object msgId) {
        System.out.println("接收到了数据  哈哈"+msgId);
    }
    //是一个让下游调用的方法，如果下游没有接收到消息，下游就可调用
    @Override
    public void fail(Object msgId) {
        System.out.println("没消息呀  呜呜"+msgId);
        //如果下游接受数据失败，在这里我们可以再次发生一次数据
        //spoutOutputCollector.emit(new Values(msgId));
    }
}
