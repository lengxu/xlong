package uyun.xianglong.app.controller;

import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/web")
public class WebController {

    /*
    查看数据接入ETL流程
    查看数据接入访问信息
    查看模型信息
    查看数据对应的分析定义：聚合分析、流分析
    查询数据
    模糊查询数据列表和数据接入列表
    数据接入列表查询
    数据列表查询
    */
    @GetMapping(value = "/test")
    Object config() {
        String res = "ok";

        return res;
    }

}
