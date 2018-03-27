package uyun.xianglong.app.message;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JobsIds {

    @SerializedName("jobs-running")
    @Expose
    private List<String> jobsRunning = null;
    @SerializedName("jobs-finished")
    @Expose
    private List<String> jobsFinished = null;
    @SerializedName("jobs-cancelled")
    @Expose
    private List<String> jobsCancelled = null;
    @SerializedName("jobs-failed")
    @Expose
    private List<String> jobsFailed = null;

    public List<String> getJobsRunning() {
        return jobsRunning;
    }

    public void setJobsRunning(List<String> jobsRunning) {
        this.jobsRunning = jobsRunning;
    }

    public List<String> getJobsFinished() {
        return jobsFinished;
    }

    public void setJobsFinished(List<String> jobsFinished) {
        this.jobsFinished = jobsFinished;
    }

    public List<String> getJobsCancelled() {
        return jobsCancelled;
    }

    public void setJobsCancelled(List<String> jobsCancelled) {
        this.jobsCancelled = jobsCancelled;
    }

    public List<String> getJobsFailed() {
        return jobsFailed;
    }

    public void setJobsFailed(List<String> jobsFailed) {
        this.jobsFailed = jobsFailed;
    }

}
