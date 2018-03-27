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
    private String type;
    private Integer nodeId;
    private List<Integer> prevNodeIds = new ArrayList<>();
    private Map<String,Object> nodeParameters = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public PipelineNodeConfig(String name, String type, Integer nodeId, List<Integer> prevNodeIds, Map<String, Object> nodeParameters) {
        this.name = name;
        this.type = type;
        this.nodeId = nodeId;
        this.prevNodeIds = prevNodeIds;
        this.nodeParameters = nodeParameters;
    }

    public PipelineNodeConfig() {
    }

    @Override
    public String toString() {
        return "PipelineNodeConfig{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", nodeId=" + nodeId +
                ", prevNodeIds=" + prevNodeIds +
                ", nodeParameters=" + nodeParameters +
                '}';
    }
}
