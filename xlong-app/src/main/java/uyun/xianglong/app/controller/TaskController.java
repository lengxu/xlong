package uyun.xianglong.app.controller;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uyun.xianglong.app.message.*;

@CrossOrigin
@RestController
public class TaskController {

    String baseUrl = "http://10.1.62.236:8081";
    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();

    private String restGet(String url) {
        try {
            return Unirest.get(baseUrl + url).asString().getBody();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    private String restPost(String url, String body) {
        try {
            return Unirest.post(url).body(body).asString().getBody();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/config")
    Config config() {
        String res = restGet("/config");
        return gson.fromJson(res, Config.class);
    }

    @GetMapping(value = "/overview")
    Overview overview() {
        String res = restGet("/overview");
        return gson.fromJson(res, Overview.class);
    }

    @GetMapping(value = "/jobs")
    JobsIds jobsWithIDsOverview() {
        String res = restGet("/jobs");
        return gson.fromJson(res, JobsIds.class);
    }

    @GetMapping(value = "/joboverview")
    Object jobsOverview() {
        // compose of runningJobs failedJobs ...
        String res = restGet("/joboverview");
        return res;
    }

    @GetMapping(value = "/joboverview/running")
    Jobs runningJobs() {
        String res = restGet("/joboverview/running");
        return gson.fromJson(res, Jobs.class);
    }

    @GetMapping(value = "/joboverview/completed")
    Jobs completedJobs() {
        String res = restGet("/joboverview/completed");
        return gson.fromJson(res, Jobs.class);
    }

    // 404, maybe deprecated
    @GetMapping(value = "/joboverview/canceled")
    Jobs canceledJobs() {
        String res = restGet("/joboverview/canceled");
        return gson.fromJson(res, Jobs.class);
    }

    // 404, maybe deprecated
    @GetMapping(value = "/joboverview/failed")
    Jobs failedJobs() {
        String res = restGet("/joboverview/failed");
        return gson.fromJson(res, Jobs.class);
    }

    @GetMapping(value = "/jobs/{jobid}")
    Object jobSummary(@PathVariable("jobid") String jobid) {
        String res = restGet("/jobs/" + jobid);
        return res;
    }

    @DeleteMapping(value = "/jobs/{jobid}/cancel")
    String cancelJob(@PathVariable("jobid") String jobid) {
        try {
            Unirest
                    .delete(baseUrl + "/jobs/" + jobid + "/cancel")
                    .asString().getBody();
            return jobid;
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/jars/upload")
    String uploadJar(@RequestParam("file") MultipartFile file) {
        try {
            Unirest.post(baseUrl + "/jars/upload")
                    .field("file", file.getInputStream(), file.getName()).asString();
            return file.getName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/jars/{jarid}/run")
    String runJar(@PathVariable("jarid") String jarid,
                  @RequestParam("entry-class") String entryClass,
                  @RequestParam("program-args") String args,
                  @RequestParam("parallelism") Integer parallelism) {
        // The jarid parameter is the file name of the program JAR in the configured web frontend upload director (configuration key jobmanager.web.upload.dir).
        try {
            String res = Unirest.post(baseUrl + "/jars/" + jarid + "/run")
                    .queryString("entry-class", entryClass)
                    .queryString("program-args", args)
                    .queryString("parallelism", parallelism).asString().getBody();
            return res;
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

}
