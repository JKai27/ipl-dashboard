package io.javabrains.ipldashboard.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping(value = {"/", "/teams/**"})
    public String redirect() {
        return "forward:/index.html";
    }
}