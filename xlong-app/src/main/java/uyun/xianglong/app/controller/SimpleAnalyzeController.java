package uyun.xianglong.app.controller;

import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/simple")
public class SimpleAnalyzeController {

    @PostMapping(value = "/save")
    Object save(Object input) {
        String res = "ok";
        return res;
    }


}
