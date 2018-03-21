package uyun.xianglong.examples.jaz.flink.table;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created By wuhuahe
 * author:游龙
 * Date: 2018-03-16
 * Time: 10:22
 * Desc: 正则提取案例
 */
public class RegrexExtractorExample {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        final StreamTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(env);

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "10.1.53.65:9192");
        props.setProperty("group.id","fink-kafka-demo");
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        final String regrex = "\\s*(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{3})-(\\d{10})\\s*(GetEntryIDfromDB|PutEntryInfo|PutEntryErrorInfo):\\s*\\[?EntryID\\]?[:|=|]?\\s?(\\w{0,18}),?\\s*\\[?StepID\\]?[:|=|]?\\s?(\\w{0,8}),?((.*[.|\\n]*)*)?";
        FlinkKafkaConsumer010<String> consumer010 = new FlinkKafkaConsumer010<String>("loginfo", new SimpleStringSchema(), props);

        TypeInformation<?>[] types = {
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.LONG_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO
        };
        String[] names = {"origin_msg", "timestamp", "sn", "recordType", "entryId", "stepId", "errMsg"};

        RowTypeInfo typeInfo = new RowTypeInfo(types, names);
        DataStream<Row> logInfos = env.addSource(consumer010).map((MapFunction<String, Row>) value -> {
            Pattern pattern = Pattern.compile(regrex);
            Matcher m = pattern.matcher(value);
            if(m.find()){
                String origin_msg = m.group(0);
                String timestamp = m.group(1);
                String serialNum = m.group(2);
                Long sn = null;
                if(StringUtils.isNotBlank(serialNum)){
                   sn = Long.parseLong(serialNum);
                }
                String recordType = m.group(3);
                String entryId = m.group(4);
                String stepId = m.group(5);
                String errMsg = m.group(6);
                return Row.of(origin_msg, timestamp, sn, recordType, entryId, stepId, errMsg);
            }
            return Row.of("","",-1L,"","","","");
        }).filter((FilterFunction<Row>) value -> {
            String field = value.getField(0).toString();
            return StringUtils.isNotBlank(field);
        }).returns(typeInfo);
        Table ptable = tableEnv.fromDataStream(logInfos, "origin_msg, timestamp, sn, recordType, entryId, stepId, errMsg");
        tableEnv.registerTable("logInfos", ptable);
        ptable.printSchema();

        Table result = tableEnv.sqlQuery("select * from logInfos");
        DataStream<Row> resultSet = tableEnv.toAppendStream(result, Row.class);
        resultSet.print();

        env.execute();

    }
}
