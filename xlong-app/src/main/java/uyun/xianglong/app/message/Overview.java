package uyun.xianglong.app.message;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Overview {

    @SerializedName("taskmanagers")
    @Expose
    private Integer taskmanagers;
    @SerializedName("slots-total")
    @Expose
    private Integer slotsTotal;
    @SerializedName("slots-available")
    @Expose
    private Integer slotsAvailable;
    @SerializedName("jobs-running")
    @Expose
    private Integer jobsRunning;
    @SerializedName("jobs-finished")
    @Expose
    private Integer jobsFinished;
    @SerializedName("jobs-cancelled")
    @Expose
    private Integer jobsCancelled;
    @SerializedName("jobs-failed")
    @Expose
    private Integer jobsFailed;

    public Integer getTaskmanagers() {
        return taskmanagers;
    }

    public void setTaskmanagers(Integer taskmanagers) {
        this.taskmanagers = taskmanagers;
    }

    public Integer getSlotsTotal() {
        return slotsTotal;
    }

    public void setSlotsTotal(Integer slotsTotal) {
        this.slotsTotal = slotsTotal;
    }

    public Integer getSlotsAvailable() {
        return slotsAvailable;
    }

    public void setSlotsAvailable(Integer slotsAvailable) {
        this.slotsAvailable = slotsAvailable;
    }

    public Integer getJobsRunning() {
        return jobsRunning;
    }

    public void setJobsRunning(Integer jobsRunning) {
        this.jobsRunning = jobsRunning;
    }

    public Integer getJobsFinished() {
        return jobsFinished;
    }

    public void setJobsFinished(Integer jobsFinished) {
        this.jobsFinished = jobsFinished;
    }

    public Integer getJobsCancelled() {
        return jobsCancelled;
    }

    public void setJobsCancelled(Integer jobsCancelled) {
        this.jobsCancelled = jobsCancelled;
    }

    public Integer getJobsFailed() {
        return jobsFailed;
    }

    public void setJobsFailed(Integer jobsFailed) {
        this.jobsFailed = jobsFailed;
    }

}
