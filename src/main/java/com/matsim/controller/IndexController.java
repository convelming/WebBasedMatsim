package com.matsim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by MingLU on 2018/4/29,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
@Controller
public class IndexController {
    @RequestMapping("/matsimBlock")
    public String toIndex(){

         return "/test";
    }
}
