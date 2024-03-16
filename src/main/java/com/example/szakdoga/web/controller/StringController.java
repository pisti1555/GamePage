package com.example.szakdoga.web.controller;

import com.example.szakdoga.service.StringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StringController {
    StringService service;

    @Autowired
    public StringController(StringService service) {
        this.service = service;
    }

    @GetMapping("/ez")
    public String getString(Model model) {
        model.addAttribute("string", service.probaString.getProba());
        return "index";
    }
}