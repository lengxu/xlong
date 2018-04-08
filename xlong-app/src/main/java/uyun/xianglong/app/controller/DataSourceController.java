package uyun.xianglong.app.controller;

import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/source")
public class DataSourceController {

    @PostMapping(value = "/save-source-kafka")
    Object saveKafka(Object input) {
        String res = "ok";
        return res;
    }

    @GetMapping(value = "/get-source-kafka")
    Object getKafka(Object input) {
        String res = "ok";
        return res;
    }

    @PostMapping(value = "/save-source-zabbix")
    Object saveZabbix(Object input) {
        String res = "ok";
        return res;
    }

    @GetMapping(value = "/get-source-zabbix")
    Object getZabbix(Object input) {
        String res = "ok";
        return res;
    }

    @PostMapping(value = "/save-source-jdbc")
    Object saveJdbc(Object input) {
        String res = "ok";
        return res;
    }

    @GetMapping(value = "/get-source-jdbc")
    Object getJdbc(Object input) {
        String res = "ok";
        return res;
    }

}
