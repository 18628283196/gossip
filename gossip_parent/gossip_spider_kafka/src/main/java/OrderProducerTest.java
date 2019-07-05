import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * 订单的生产者代码
 */
public class OrderProducerTest {
    public static void main(String[] args) throws InterruptedException {
        // 2、发送数据-topic:order，value
        Properties props = new Properties();
        props.put("bootstrap.servers", "node01:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        // 1、连接集群，通过配置文件的方式
        KafkaProducer<String,String> kafkaProducer = new KafkaProducer<String, String>(props);
       for(int i = 0;i<100;i++){
           //一直发送数据，需要有一个producerRecoerd对象
           ProducerRecord record = new ProducerRecord<String,String>("order","订单信息"+i);
           kafkaProducer.send(record);
           //kafkaProducer.flush();//刷新
           Thread.sleep(1000);
       }
    }
}

