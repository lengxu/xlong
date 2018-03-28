package uyun.xianglong.sdk.node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By wuhuahe
 * author:游龙
 * Date: 2018-03-27
 * Time: 10:22
 * Desc: 节点参数规范
 */
public class PipelineNodeParameter {
    private String name;
    private String desc;
    private String valueType;
    private boolean isRequired;
    private String defaultValue;
    private List<String> validValues = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<String> getValidValues() {
        return validValues;
    }

    public void setValidValues(List<String> validValues) {
        this.validValues = validValues;
    }

    public PipelineNodeParameter() {
    }

    public PipelineNodeParameter(String name, String desc, String valueType, boolean isRequired, String defaultValue, List<String> validValues) {
        this.name = name;
        this.desc = desc;
        this.valueType = valueType;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
        this.validValues = validValues;
    }

    @Override
    public String toString() {
        return "PipelineNodeParameter{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", valueType='" + valueType + '\'' +
                ", isRequired=" + isRequired +
                ", defaultValue='" + defaultValue + '\'' +
                ", validValues=" + validValues +
                '}';
    }
}
