package project.gamepage.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import project.gamepage.service.invitations.InvitationService;
import project.gamepage.service.UserFriendsService;

import java.security.Principal;

@Controller
public class IndexController {
    private final InvitationService invitationService;
    private final UserFriendsService friendsService;

    @Autowired
    public IndexController(InvitationService invitationService, UserFriendsService friendsService) {
        this.invitationService = invitationService;
        this.friendsService = friendsService;
    }

    @GetMapping
    public String getIndexPage(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);
        System.out.println("\n\n\n\n" + username + " game invites: " + invitationService.getInvites(username) + "\n\n\n\n");
        return "index";
    }
}
