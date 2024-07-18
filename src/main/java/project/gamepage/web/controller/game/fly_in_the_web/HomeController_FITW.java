package project.gamepage.web.controller.game.fly_in_the_web;

import project.gamepage.service.ProfileDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/fly-in-the-web")
public class HomeController_FITW {
    private final ProfileDataService profileDataService;

    @Autowired
    public HomeController_FITW(ProfileDataService profileDataService) {
        this.profileDataService = profileDataService;
    }

    @GetMapping
    public String getHomePage_FITW(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "/game/fly_in_the_web/home";
    }

    @GetMapping("/scoreboard")
    public String getScoreboard_FITW(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("users", profileDataService.getAll());
        return "/game/fly_in_the_web/scoreboard";
    }
}
