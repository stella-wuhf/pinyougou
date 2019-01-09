package cn.itcast.core.controller;

import cn.itcast.core.service.IndexService;
import com.alibaba.dubbo.config.annotation.Reference;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("index")
public class IndexController {

    @Reference
    private IndexService indexService;


    @RequestMapping("findindexCatList")
    public Map<String, Object> findindexCatList() {


        return indexService.findindexCatList();


    }


}
