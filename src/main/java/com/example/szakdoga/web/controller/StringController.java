package com.example.szakdoga.web.controller;

import com.example.szakdoga.service.StringService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class StringController {
    StringService service;

    @Autowired
    public StringController(StringService service) {
        this.service = service;
    }

    @GetMapping("/ez")
    public String getString(Model model, Principal principal) {
        service.connectionService.createString(principal.getName());
        model.addAttribute("string", service.connectionService.getStringFromUser(principal.getName()));

        return "index";
    }

    @GetMapping("/{id}")
    public String getUserString(Model model, @PathVariable("id") String username) {
        model.addAttribute("string", service.connectionService.getStringFromUser(username));
        return "index";
    }
}