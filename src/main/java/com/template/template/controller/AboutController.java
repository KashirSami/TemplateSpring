package com.template.template.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {
    //Sobre mi
    @GetMapping("about")
    public String about() {
        return "about";
    }
}
