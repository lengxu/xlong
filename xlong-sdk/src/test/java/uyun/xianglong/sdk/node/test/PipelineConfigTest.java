package uyun.xianglong.sdk.node.test;

import org.junit.Assert;
import org.junit.Test;
import uyun.xianglong.sdk.pipeline.PipelineConfig;

/**
 * Created By wuhuahe
 * author:游龙
 * Date: 2018-03-28
 * Time: 10:28
 * Desc:
 */
public class PipelineConfigTest {
    @Test
    public void testParse(){
        String pipelineConfig = "{\n" +
                "  \"name\": \"examplePipeline\",\n" +
                "  \"runnerParameters\": {\n" +
                "    \"flink.parameter.aaa\":\"vvv\",\n" +
                "    \"flink.parameter.bbb\":true,\n" +
                "    \"flink.parameter.ccc\": 12,\n" +
                "    \"flink.parameter.rate\": 34.56\n" +
                "  },\n" +
                "  \"nodes\": [\n" +
                "    {\n" +
                "      \"name\":\"uyun指标数据输入\",\n" +
                "      \"type\":\"kafkaJsonInputOp\",\n" +
                "      \"nodeId\":1,\n" +
                "      \"prevNodeIds\":[-1],\n" +
                "      \"nodeParameters\":{\n" +
                "        \"bootstrap.servers\":\"10.1.61.106:9192\",\n" +
                "        \"group.id\":\"consumerGroup1\",\n" +
                "        \"topic\":\"metrics\",\n" +
                "        \"outputDatasetName\":\"CONSUME\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\":\"uyun资源数据输入\",\n" +
                "      \"type\":\"kafkaJsonInputOp\",\n" +
                "      \"nodeId\":1,\n" +
                "      \"prevNodeIds\":[-1],\n" +
                "      \"nodeParameters\":{\n" +
                "        \"bootstrap.servers\":\"10.1.61.106:9192\",\n" +
                "        \"group.id\":\"consumerGroup2\",\n" +
                "        \"topic\":\"resources\",\n" +
                "        \"outputDatasetName\":\"CONSUME\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\":\"数据关联\",\n" +
                "      \"type\":\"JoinOperator\",\n" +
                "      \"nodeId\":1,\n" +
                "      \"prevNodeIds\":[-1],\n" +
                "      \"nodeParameters\":{\n" +
                "        \"joinType\":\"innerJoin\",\n" +
                "        \"joinCondition\":[\n" +
                "          {\n" +
                "            \"leftDatasetField\":\"resourceId\",\n" +
                "            \"rightDatasetField\":\"resourceId\",\n" +
                "            \"relation\":\"=\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"leftDatasetField\":\"resourceId2\",\n" +
                "            \"rightDatasetField\":\"resourceId\",\n" +
                "            \"relation\":\"=\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\":\"客户信息聚合分析结果kakfa输出\",\n" +
                "      \"type\":\"kafkaJsonOutputOp\",\n" +
                "      \"nodeId\":2,\n" +
                "      \"prevNodeIds\":[1],\n" +
                "      \"nodeParameters\":{\n" +
                "        \"bootstrap.servers\":\"10.1.61.106:9192\",\n" +
                "        \"topic\":\"c\",\n" +
                "        \"outputDatasetName\":\"RESULT\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        PipelineConfig config = PipelineConfig.parse(pipelineConfig);
        System.out.println(config);
        Assert.assertEquals("examplePipeline", config.getName());
        Assert.assertEquals(12.0, config.getRunnerParameters().get("flink.parameter.ccc"));
    }

    @Test
    public void testParseFromFile(){
        PipelineConfig config = PipelineConfig.parseFromFile("pipeline_example.json");
        System.out.println(config);
        config.getNodes().stream().filter(n -> n.getName().equals("资源数据与指标数据关联")).forEach(n ->{
            System.out.println(n.getNodeParameters().get("joinCondition"));
        });
        Assert.assertEquals("examplePipeline", config.getName());
        Assert.assertEquals(12.0, config.getRunnerParameters().get("flink.parameter.ccc"));
    }
}
