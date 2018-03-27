package uyun.xianglong.app.message;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Jobs {

  @SerializedName("jobs")
  @Expose
  List<Summary> jobs;

  public List<Summary> getJobs() {
    return jobs;
  }

  public Jobs setJobs(List<Summary> jobs) {
    this.jobs = jobs;
    return this;
  }
}
