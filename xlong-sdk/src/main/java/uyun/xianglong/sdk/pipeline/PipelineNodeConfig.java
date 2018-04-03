package uyun.xianglong.sdk.pipeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created By wuhuahe
 * author:游龙
 * Date: 2018-03-27
 * Time: 10:58
 * Desc:
 */
public class PipelineNodeConfig implements Serializable{
    private String name;
    private String javaImplClass;
    private Integer nodeId;
    private List<Integer> prevNodeIds = new ArrayList<>();
    private Map<String,Object> nodeParameters = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public List<Integer> getPrevNodeIds() {
        return prevNodeIds;
    }

    public void setPrevNodeIds(List<Integer> prevNodeIds) {
        this.prevNodeIds = prevNodeIds;
    }

    public Map<String, Object> getNodeParameters() {
        return nodeParameters;
    }

    public void setNodeParameters(Map<String, Object> nodeParameters) {
        this.nodeParameters = nodeParameters;
    }

    public String getJavaImplClass() {
        return javaImplClass;
    }

    public void setJavaImplClass(String javaImplClass) {
        this.javaImplClass = javaImplClass;
    }

    public PipelineNodeConfig() {
    }

    public PipelineNodeConfig(String name, String javaImplClass, Integer nodeId, List<Integer> prevNodeIds, Map<String, Object> nodeParameters) {
        this.name = name;
        this.javaImplClass = javaImplClass;
        this.nodeId = nodeId;
        this.prevNodeIds = prevNodeIds;
        this.nodeParameters = nodeParameters;
    }

    @Override
    public String toString() {
        return "PipelineNodeConfig{" +
                "name='" + name + '\'' +
                ", javaImplClass='" + javaImplClass + '\'' +
                ", nodeId=" + nodeId +
                ", prevNodeIds=" + prevNodeIds +
                ", nodeParameters=" + nodeParameters +
                '}';
    }
}
