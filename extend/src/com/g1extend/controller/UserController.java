package com.g1extend.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({ "/user" })
public class UserController {
    @RequestMapping({ "/test" })
    public ModelAndView getFormModal(HttpServletRequest request) {
        System.out.println("osjdakjdksjjjjjjjjjjjjjjjjjjjjj");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/Qrcode");
        return modelAndView;
    }
}
