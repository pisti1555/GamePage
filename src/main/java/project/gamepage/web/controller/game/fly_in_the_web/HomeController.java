package project.gamepage.web.controller.game.fly_in_the_web;

import project.gamepage.service.InvitationService;
import project.gamepage.service.UserFriendsService;
import project.gamepage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/fly-in-the-web")
public class HomeController {
    UserService userService;
    UserFriendsService friendsService;
    InvitationService invitationService;

    @Autowired
    public HomeController(UserService userService, UserFriendsService friendsService, InvitationService invitationService) {
        this.userService = userService;
        this.friendsService = friendsService;
        this.invitationService = invitationService;
    }

    @GetMapping
    public String getHomePage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("invites", invitationService.getInvites(principal.getName()));
        model.addAttribute("invCount", invitationService.invCount(principal.getName()));
        model.addAttribute("friendRequests", friendsService.getFriendRequests(principal.getName()));
        model.addAttribute("friendRequestCount", friendsService.getFriendRequestCount(principal.getName()));
        return "/game/fly_in_the_web/home";
    }

    @GetMapping("/scoreboard")
    public String getScoreboard(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("invites", invitationService.getInvites(principal.getName()));
        model.addAttribute("invCount", invitationService.invCount(principal.getName()));
        model.addAttribute("friendRequests", friendsService.getFriendRequests(principal.getName()));
        model.addAttribute("friendRequestCount", friendsService.getFriendRequestCount(principal.getName()));
        return "/game/fly_in_the_web/scoreboard";
    }

}
