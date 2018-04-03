package uyun.xianglong.sdk.pipeline;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created By wuhuahe
 * author:游龙
 * Date: 2018-03-27
 * Time: 10:57
 * Desc:
 */
public class PipelineConfig implements Serializable{
    private String name;
    private Map<String,Object> runnerParameters = new HashMap<String,Object>();
    private List<PipelineNodeConfig> nodes = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getRunnerParameters() {
        return runnerParameters;
    }

    public void setRunnerParameters(Map<String, Object> runnerParameters) {
        this.runnerParameters = runnerParameters;
    }

    public List<PipelineNodeConfig> getNodes() {
        return nodes;
    }

    public void setNodes(List<PipelineNodeConfig> nodes) {
        this.nodes = nodes;
    }

    public PipelineConfig() {
    }

    public PipelineConfig(String name, Map<String, Object> runnerParameters, List<PipelineNodeConfig> nodes) {
        this.name = name;
        this.runnerParameters = runnerParameters;
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "PipelineConfig{" +
                "name='" + name + '\'' +
                ", runnerParameters=" + runnerParameters +
                ", nodes=" + nodes +
                '}';
    }

    /**
     * 获取pipeline参数
     * @param pipelineConfig
     * @return
     */
    public static PipelineConfig parse(String pipelineConfig){
        if(StringUtils.isBlank(pipelineConfig)){
            throw new RuntimeException("PipelineConfig must not be null or blank");
        }
        return new Gson().fromJson(pipelineConfig, PipelineConfig.class);
    }

    public static PipelineConfig parseFromFile(String pipelineConfigFilePath){
        if(StringUtils.isBlank(pipelineConfigFilePath)){
            throw new RuntimeException("pipelineConfigFilePath must not be null or blank.");
        }
        String absoulteFilePath = pipelineConfigFilePath;
        if(!pipelineConfigFilePath.startsWith("/") &&  !pipelineConfigFilePath.contains(":")){
            absoulteFilePath = Thread.currentThread().getContextClassLoader().getResource(pipelineConfigFilePath).getPath();
        }
        File file = new File(absoulteFilePath);
        StringBuilder pipelineConfig = new StringBuilder("");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = br.readLine();
            while(line != null){
                pipelineConfig.append(line);
                line = br.readLine();
            }
            return parse(pipelineConfig.toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Not found pipelineconfig file in " + absoulteFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
