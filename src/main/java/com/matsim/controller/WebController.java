package com.matsim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by MingLU on 2018/5/1,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */


@Controller
@CrossOrigin
@RequestMapping("")
public class WebController {
    @RequestMapping("/")
    public String toIndex(){
        return "redirect:/static/web/index.html";
    }
}
