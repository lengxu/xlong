package uyun.xianglong.app.controller;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Test;

import static org.junit.Assert.*;

public class AppControllerTest {

    String baseUrl = "http://localhost:8080";

    private String restGet(String url) {
        try {
            return Unirest.get(baseUrl + url).asString().getBody();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    private String restPost(String url, String body) throws UnirestException {
        return Unirest.post(url).body(body).asString().getBody();
    }

    private void printResult(Object res) {
        System.out.println("------------------------------------------------------");
        System.out.println(res);
        System.out.println("------------------------------------------------------");
    }

    @Test
    public void config() {
        String res = restGet("/config");
        printResult(res);
    }

    @Test
    public void overview() {
        String res = restGet("/overview");
        printResult(res);
    }

    @Test
    public void jobsWithIDsOverview() {
        String res = restGet("/jobs");
        printResult(res);
    }

    @Test
    public void jobsOverview() {
        String res = restGet("/joboverview");
        printResult(res);
    }

    @Test
    public void runningJobs() {
        String res = restGet("/joboverview/running");
        printResult(res);
    }

    @Test
    public void completedJobs() {
        String res = restGet("/joboverview/completed");
        printResult(res);
    }

//    @Test
//    public void canceledJobs() {
//        String res = restGet("/joboverview/canceled");
//        printResult(res);
//    }

//    @Test
//    public void failedJobs() {
//        String res = restGet("/joboverview/failed");
//        printResult(res);
//    }

    @Test
    public void jobSummary() {
        String res = restGet("/jobs/de810daf636ada280785ba8976eb9379");
        printResult(res);
    }

//    @Test
//    public void jobExceptions() {
//        String res = restGet("placeholder");
//        printResult(res);
//    }

    @Test
    public void cancelJob() {
        String jobid = "123zxcacasc";
        String res = null;
        try {
            res = Unirest
                    .delete(baseUrl + "/jobs/" + jobid + "/cancel")
                    .asString().getBody();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        printResult(res);
    }

    @Test
    public void cancelJobWithSavePoint1() {
        String res = restGet("placeholder");
        printResult(res);
    }

    @Test
    public void uploadJar() {
        HttpResponse<String> res = null;
        try {
            res = Unirest.post(baseUrl+"/jars/upload").asString();
            printResult(res);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        printResult(res);
    }

    @Test
    public void runJar() {
        String res = restGet("placeholder");
        printResult(res);
    }

    @Test
    public void test1() {
        String res = restGet("placeholder");
        printResult(res);
    }
}
