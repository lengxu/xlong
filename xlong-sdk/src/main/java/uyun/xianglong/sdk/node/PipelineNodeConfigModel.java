package uyun.xianglong.sdk.node;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created By wuhuahe
 * author:游龙
 * Date: 2018-03-27
 * Time: 10:27
 * Desc: PipelineNode配置模板
 */
public class PipelineNodeConfigModel {
    private String type;
    private String zhType;
    private String image;
    private String pipelineNodeImplClass;
    private String pipelineNodeCategory;
    private List<PipelineNodeParameter> pipelineNodeParameters = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getZhType() {
        return zhType;
    }

    public void setZhType(String zhType) {
        this.zhType = zhType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPipelineNodeImplClass() {
        return pipelineNodeImplClass;
    }

    public void setPipelineNodeImplClass(String pipelineNodeImplClass) {
        this.pipelineNodeImplClass = pipelineNodeImplClass;
    }

    public String getPipelineNodeCategory() {
        return pipelineNodeCategory;
    }

    public void setPipelineNodeCategory(String pipelineNodeCategory) {
        this.pipelineNodeCategory = pipelineNodeCategory;
    }

    public List<PipelineNodeParameter> getPipelineNodeParameters() {
        return pipelineNodeParameters;
    }

    public void setPipelineNodeParameters(List<PipelineNodeParameter> pipelineNodeParameters) {
        this.pipelineNodeParameters = pipelineNodeParameters;
    }

    public PipelineNodeConfigModel() {
    }

    public PipelineNodeConfigModel(String type, String zhType, String image, String pipelineNodeImplClass, String pipelineNodeCategory, List<PipelineNodeParameter> pipelineNodeParameters) {
        this.type = type;
        this.zhType = zhType;
        this.image = image;
        this.pipelineNodeImplClass = pipelineNodeImplClass;
        this.pipelineNodeCategory = pipelineNodeCategory;
        this.pipelineNodeParameters = pipelineNodeParameters;
    }

    @Override
    public String toString() {
        return "PipelineNodeConfigModel{" +
                "type='" + type + '\'' +
                ", zhType='" + zhType + '\'' +
                ", image='" + image + '\'' +
                ", pipelineNodeImplClass='" + pipelineNodeImplClass + '\'' +
                ", pipelineNodeCategory='" + pipelineNodeCategory + '\'' +
                ", pipelineNodeParameters=" + pipelineNodeParameters +
                '}';
    }

    /**
     * 解析字符串获取PipelineNode配置模板
     * @param jsonConfig
     * @return
     */
    public static PipelineNodeConfigModel parse(String jsonConfig){
        if(StringUtils.isBlank(jsonConfig)){
            throw new RuntimeException("the pipeline node config model string must not be null or blank");
        }
        return new Gson().fromJson(jsonConfig, PipelineNodeConfigModel.class);
    }

    private static PipelineNodeConfigModel parseFromFilePathSingle(File configFile){
        StringBuilder pipelineConfig = new StringBuilder("");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
            String line = br.readLine();
            while(line != null){
                pipelineConfig.append(line);
                line = br.readLine();
            }
            return parse(pipelineConfig.toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Not found pipelineconfig file in " + configFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从目录中获取操作节点配置模板（各个操作节点配置参数规范）
     * @param pipelineNodeConfigFilePath
     * @return
     */
    public static List<PipelineNodeConfigModel> parseFromFilePath(String pipelineNodeConfigFilePath){
        if(StringUtils.isBlank(pipelineNodeConfigFilePath)){
            throw new RuntimeException("pipelineConfigFilePath must not be null or blank.");
        }
        final String absoulteFilePath;
        if(!pipelineNodeConfigFilePath.startsWith("/") &&  !pipelineNodeConfigFilePath.contains(":")){
            absoulteFilePath = Thread.currentThread().getContextClassLoader().getResource(pipelineNodeConfigFilePath).getPath();
        }else {
            absoulteFilePath = pipelineNodeConfigFilePath;
        }
        File file = new File(absoulteFilePath);
        if(file.isDirectory()){
            File[] nodeconfigFiles = file.listFiles();
            return Arrays.stream(nodeconfigFiles).map(f -> parseFromFilePathSingle(f)).collect(Collectors.toList());
        }else{
            List<PipelineNodeConfigModel> models = new ArrayList<>();
            models.add(parseFromFilePathSingle(file));
            return models;
        }
    }
}
