package cn.itcast.demo;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.jdbc.bolt.JdbcInsertBolt;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import org.apache.storm.jdbc.mapper.JdbcMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcMapper;
import org.apache.storm.shade.com.google.common.collect.Lists;
import org.apache.storm.topology.TopologyBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 组装topology结构，将代码传到集群上运行
 */
public class WordCountTopology {
    public static void main(String[] args) throws Exception {
        //组装topology的结构
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        //1.1设置spout参数 参数一：组件名称(随便起)参数二：spout对象
        topologyBuilder.setSpout("rfspout",new ReadFileSpout(),1);
        //1.2设置splitbolt  分组撤了，上游该如何向下游发送数据
        topologyBuilder.setBolt("slbolt",new SplitBolte(),1).shuffleGrouping("rfspout");
        //设置wordCountBolt
        //topologyBuilder.setBolt("wcbolt",new WordCountBolt(),1).shuffleGrouping("slbolt");


        //创建
        Map hikariConfigMap = new HashMap();
        hikariConfigMap.put("dataSourceClassName","com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikariConfigMap.put("dataSource.url", "jdbc:mysql://localhost/log_monitor");
        hikariConfigMap.put("dataSource.user","root");
        hikariConfigMap.put("dataSource.password","root");
        ConnectionProvider connectionProvider = new HikariCPConnectionProvider(hikariConfigMap);

        String tableName = "user";//表名
        List<Column> columnSchema = Lists.newArrayList(
                new Column("userId", java.sql.Types.INTEGER),
                new Column("name", java.sql.Types.VARCHAR),
                new Column("age", java.sql.Types.INTEGER));
        JdbcMapper simpleJdbcMapper = new SimpleJdbcMapper(columnSchema);
      /*JdbcInsertBolt userPersistanceBolt = new JdbcInsertBolt(connectionProvider, simpleJdbcMapper)
                                          .withTableName("welc")
                                          .withQueryTimeoutSecs(30);
                                          Or*/
        JdbcInsertBolt userPersistanceBolt = new JdbcInsertBolt(connectionProvider, simpleJdbcMapper)
                .withInsertQuery("insert into user values (?,?,?)")
                .withQueryTimeoutSecs(30);
        //1.3设置jdbcbolt
        topologyBuilder.setBolt("userPersistanceBolt",userPersistanceBolt).localOrShuffleGrouping("slbolt");


        //2.将代码传到集群上运行
        //运行有两种方式 1种是在本地上运行IDEA  2.集群运行
        Config config = new Config();
        config.setMaxSpoutPending(3000);//如果内存中有3000条数据发送失败了，namespout就不再发送数据了
        //如果在本地启动
        config.setNumWorkers(1);//也可以不用设置，如果只是改这里，那么打印出来的数据会有问题，这里就是map集合的线程安全问题（并发问题）
        if(args != null && args.length > 0){
            //集群启动
            StormSubmitter.submitTopology(args[0],config,topologyBuilder.createTopology());
        }else {
            //本地启动
            LocalCluster localCluster = new LocalCluster();
            //参数1：topology名称 字符串  参数2：map storm集群配置对象 参数3：topology对象
            localCluster.submitTopology("wordcount",config,topologyBuilder.createTopology());

        }

    }
}
