package uyun.xianglong.app.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import uyun.xianglong.app.input.JdbcInfo;
import uyun.xianglong.app.input.KafkaInfo;
import uyun.xianglong.app.input.ZabbixInfo;

@CrossOrigin
@RestController
@RequestMapping(value = "/source")
public class DataSourceController {

    @ApiOperation(value = "", notes = "更新的时候带id 新建的时候不用带id")
    @PostMapping(value = "/save-source-kafka")
    Object saveKafka(KafkaInfo input) {
        String res = "ok";
        return res;
    }

    @GetMapping(value = "/get-source-kafka")
    KafkaInfo getKafka(KafkaInfo input) {
        String res = "ok";
        return new KafkaInfo();
    }

    @ApiOperation(value = "", notes = "更新的时候带id 新建的时候不用带id")
    @PostMapping(value = "/save-source-zabbix")
    Object saveZabbix(ZabbixInfo input) {
        String res = "ok";
        return res;
    }

    @GetMapping(value = "/get-source-zabbix")
    ZabbixInfo getZabbix(ZabbixInfo input) {
        String res = "ok";
        return new ZabbixInfo();
    }

    @ApiOperation(value = "", notes = "更新的时候带id 新建的时候不用带id")
    @PostMapping(value = "/save-source-jdbc")
    Object saveJdbc(JdbcInfo input) {
        String res = "ok";
        return res;
    }

    @GetMapping(value = "/get-source-jdbc")
    JdbcInfo getJdbc(JdbcInfo input) {
        String res = "ok";
        return new JdbcInfo();
    }

}
