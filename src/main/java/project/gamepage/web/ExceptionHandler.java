package project.gamepage.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler
    public String handleExceptions(Exception e, HttpServletRequest request) {
        if (request.getUserPrincipal() == null) {
            return "redirect:/login";
        }

        if (request.getAttribute("javax.servlet.error.status_code") != null) {
            int status = (int) request.getAttribute("javax.servlet.error.status_code");
            return switch (status) {
                case 400 -> "400";
                case 401 -> "401";
                case 403 -> "403";
                case 404 -> "404";
                case 500 -> "500";
                default -> "error";
            };
        }

        return "error";
    }
}
