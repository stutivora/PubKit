package com.roquito.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Created by puran on 1/16/15.
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String index(Map<String, Object> model) {
        return "index";
    }
    
    @RequestMapping("/echo")
    public String echo(Map<String, Object> model) {
        return "websocket";
    }
}
