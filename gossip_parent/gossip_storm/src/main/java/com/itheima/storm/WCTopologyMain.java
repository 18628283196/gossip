package com.itheima.storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.TopologyBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class WCTopologyMain {
    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        //构建topology
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        KafkaSpoutConfig.Builder<String, String> builder = KafkaSpoutConfig.builder("node01:9092", "logs");
        KafkaSpoutConfig<String, String> kafkaSpoutConfig = builder.build();
        //消费组
        builder.setGroupId("hello_storm");
        //创建kafkaspout对象
        KafkaSpout<String, String> kafkaspout = new KafkaSpout<>(kafkaSpoutConfig);
        //设置kafkaspout
        topologyBuilder.setSpout("kafkaspout",kafkaspout);
        //设置splitbolt
        topologyBuilder.setBolt("splitbolt",new SplitBolt()).shuffleGrouping("kafkaspout");
        //设置wordcountbolt
        topologyBuilder.setBolt("wordcountBolt",new WordCountBolt()).shuffleGrouping("splitbolt");
        //设置kafkabolt
        Properties props = new Properties();
        props.put("bootstrap.servers","node01:9092");
        props.put("topic", "keywords");
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        KafkaBolt<String, String> kafkabolt = new KafkaBolt<String, String>().withProducerProperties(props);
        topologyBuilder.setBolt("kafkabolt",kafkabolt).shuffleGrouping("wordcountBolt");

        //上传代码到集群，运行
        Config config = new Config();
        Map<String, String> map = new HashMap<>();
        map.put("metadata.broker.list", "node01:9092");// 配置Kafka broker地址
        map.put("bootstrap.servers","node01:9092");
        config.put("kafka.broker.properties", map);// 配置KafkaBolt中的kafka.broker.properties
        config.put("topic", "keywords");// 配置KafkaBolt生成的topic

        if (args != null && args.length>0){
            System.out.println("集群启动");
            //集群启动
            StormSubmitter.submitTopology(args[0],config,topologyBuilder.createTopology());
        }else {
            System.out.println("本地运行");
            LocalCluster localCluster = new LocalCluster();

            localCluster.submitTopology("wordcount",config,topologyBuilder.createTopology());



        }
    }
}
