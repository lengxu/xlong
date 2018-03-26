package uyun.xianglong.app.message;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config {

    @SerializedName("refresh-interval")
    @Expose
    private Integer refreshInterval;
    @SerializedName("timezone-offset")
    @Expose
    private Integer timezoneOffset;
    @SerializedName("timezone-name")
    @Expose
    private String timezoneName;
    @SerializedName("flink-version")
    @Expose
    private String flinkVersion;
    @SerializedName("flink-revision")
    @Expose
    private String flinkRevision;

    public Integer getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(Integer refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public Integer getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(Integer timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public String getTimezoneName() {
        return timezoneName;
    }

    public void setTimezoneName(String timezoneName) {
        this.timezoneName = timezoneName;
    }

    public String getFlinkVersion() {
        return flinkVersion;
    }

    public void setFlinkVersion(String flinkVersion) {
        this.flinkVersion = flinkVersion;
    }

    public String getFlinkRevision() {
        return flinkRevision;
    }

    public void setFlinkRevision(String flinkRevision) {
        this.flinkRevision = flinkRevision;
    }

}
