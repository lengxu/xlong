package uyun.xianglong.app.message;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tasks {

    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("created")
    @Expose
    private Integer created;
    @SerializedName("scheduled")
    @Expose
    private Integer scheduled;
    @SerializedName("deploying")
    @Expose
    private Integer deploying;
    @SerializedName("running")
    @Expose
    private Integer running;
    @SerializedName("finished")
    @Expose
    private Integer finished;
    @SerializedName("canceling")
    @Expose
    private Integer canceling;
    @SerializedName("canceled")
    @Expose
    private Integer canceled;
    @SerializedName("failed")
    @Expose
    private Integer failed;
    @SerializedName("reconciling")
    @Expose
    private Integer reconciling;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public Integer getScheduled() {
        return scheduled;
    }

    public void setScheduled(Integer scheduled) {
        this.scheduled = scheduled;
    }

    public Integer getDeploying() {
        return deploying;
    }

    public void setDeploying(Integer deploying) {
        this.deploying = deploying;
    }

    public Integer getRunning() {
        return running;
    }

    public void setRunning(Integer running) {
        this.running = running;
    }

    public Integer getFinished() {
        return finished;
    }

    public void setFinished(Integer finished) {
        this.finished = finished;
    }

    public Integer getCanceling() {
        return canceling;
    }

    public void setCanceling(Integer canceling) {
        this.canceling = canceling;
    }

    public Integer getCanceled() {
        return canceled;
    }

    public void setCanceled(Integer canceled) {
        this.canceled = canceled;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public Integer getReconciling() {
        return reconciling;
    }

    public void setReconciling(Integer reconciling) {
        this.reconciling = reconciling;
    }

}
