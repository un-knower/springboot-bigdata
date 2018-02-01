package com.cmsz.springboot.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by le on 2018/2/1.
 */
@RestController
public class IndexController {
    @RequestMapping(value = "/",produces = MediaType.TEXT_PLAIN_VALUE,method = RequestMethod.GET)
    public String index(){
        return "ok";
    }

}
