package uyun.xianglong.app.message;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Summary {

    @SerializedName("jid")
    @Expose
    private String jid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("start-time")
    @Expose
    private Long startTime;
    @SerializedName("end-time")
    @Expose
    private Long endTime;
    @SerializedName("duration")
    @Expose
    private Long duration;
    @SerializedName("last-modification")
    @Expose
    private Long lastModification;
    @SerializedName("tasks")
    @Expose
    private Tasks tasks;

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getLastModification() {
        return lastModification;
    }

    public void setLastModification(Long lastModification) {
        this.lastModification = lastModification;
    }

    public Tasks getTasks() {
        return tasks;
    }

    public void setTasks(Tasks tasks) {
        this.tasks = tasks;
    }

}
