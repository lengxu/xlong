package uyun.xianglong.app.controller;

import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/advance")
public class AdvanceAnalyzeController {

    @PostMapping(value = "/save")
    Object save(Object input) {
        String res = "ok";
        return res;
    }


}
