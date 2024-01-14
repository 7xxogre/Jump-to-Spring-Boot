package com.example.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainContainer {
    @GetMapping("/sbb")
    @ResponseBody
    public String index() {
        return "index";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/question/list";
    }
}
