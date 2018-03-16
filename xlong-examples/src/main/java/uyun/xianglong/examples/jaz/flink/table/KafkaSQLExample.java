package uyun.xianglong.examples.jaz.flink.table;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

/**
 * Created By wuhuahe
 * author:游龙
 * Date: 2018-03-15
 * Time: 21:00
 * Desc:
 */
public class KafkaSQLExample {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        final StreamTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(env);

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "10.1.53.65:9192");
        props.setProperty("group.id","fink-kafka-demo");
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        FlinkKafkaConsumer010<String> consumer010 = new FlinkKafkaConsumer010<String>("consumers", new SimpleStringSchema(), props);
        TypeInformation<?>[] types_consumer = {
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.DOUBLE_TYPE_INFO};
        String[] names_consumer = {"memberId", "amt"};

        RowTypeInfo typeInfo_consumer = new RowTypeInfo(types_consumer, names_consumer);
        DataStream<Row> consumers = env.addSource(consumer010).map(new MapFunction<String, Row>() {
            @Override
            public Row map(String value) throws Exception {
                JSONObject json = JSON.parseObject(value);
                //{"memberId":"00001","amt":2.35}
                String memberId = json.getString("memberId");
                Double amt = json.getDouble("amt");
                return Row.of(memberId, amt);
            }
        }).returns(typeInfo_consumer);
        Table cTable = tableEnv.fromDataStream(consumers, "memberId,amt");
        tableEnv.registerTable("consumers", cTable);
        cTable.printSchema();


        TypeInformation<?>[] types = {
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.INT_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO};
        String[] names = {"memberId", "name", "gender", "age", "occupation"};

        RowTypeInfo typeInfo = new RowTypeInfo(types, names);
        DataStreamSource<String> ps = env.readTextFile("F:\\idea_workspace\\xlong\\xlong-examples\\datas\\persons.json");
        DataStream<Row> persons = ps.map(new MapFunction<String, Row>() {
            @Override
            public Row map(String value) throws Exception {
                JSONObject json = JSON.parseObject(value);
                //{"memberId":"00001","name":"Merry","gender":"F","age":34,"ocupation":"Doctor"}
                String memberId = json.getString("memberId");
                String name = json.getString("name");
                String gender = json.getString("gender");
                Integer age = json.getInteger("age");
                String occupation = json.getString("ocupation");
                return Row.of(memberId, name, gender, age, occupation);
            }
        }).returns(typeInfo);

        Table ptable = tableEnv.fromDataStream(persons, "memberId,name,gender,age,ocupation");
        tableEnv.registerTable("persons", ptable);
        ptable.printSchema();
        Table result = tableEnv.sqlQuery("SELECT gender,sum(age) as age_sum, avg(age) as age_avg FROM persons group by gender");
        DataStream<Tuple2<Boolean, Row>> resultSet = tableEnv.toRetractStream(result, Row.class);
        resultSet.print();

        //cTable.join(ptable).where("memberId = memberId");
        //Table joinResult = tableEnv.sqlQuery("select p.gender, sum(c.amt) as amt_sum, avg(c.amt) as amt_avg from consumers c join persons p on c.memberId = p.memberId group by p.gender");
        //tableEnv.toAppendStream(joinResult, Row.class).print();
        env.execute();
    }
}
