package com.example.szakdoga.web.controller;

import com.example.szakdoga.data.model.game.string.ProbaString;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class HomeController {

    @GetMapping("/")
    public String getHomePage(Model model, HttpSession session) {
        return "index";
    }
}
