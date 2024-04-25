package com.example.szakdoga.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler
    public String handleExceptions(Exception e, HttpServletRequest request) {
        if (request.getUserPrincipal() == null) {
            return "redirect:/login";
        }

        return "error";
    }
}
