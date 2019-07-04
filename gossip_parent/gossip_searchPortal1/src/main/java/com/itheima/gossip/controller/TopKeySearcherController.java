package com.itheima.gossip.controller;

import com.itheima.gossip.service.TopKeySearcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class TopKeySearcherController {
    @Autowired
    private TopKeySearcherService topKeySearcherService;
    @RequestMapping("/top")
    @ResponseBody
    public List<Map<String,Object>> topkeyFindByNum(Integer num){
        //判断是否正确传递
        if(num == null || num <=0){
            num = 5;
        }
        //调用service层
        List<Map<String, Object>> list = topKeySearcherService.toKeyFindByNum(num);
        return list;
    }
}
