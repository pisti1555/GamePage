package project.gamepage.web.controller.user;

import project.gamepage.data.model.user.User;
import project.gamepage.service.invitations.InvitationService;
import project.gamepage.service.UserFriendsService;
import project.gamepage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    UserService service;
    UserFriendsService friendsService;
    InvitationService invitationService;

    @Autowired
    public ProfileController(UserService service, UserFriendsService friendsService, InvitationService invitationService) {
        this.service = service;
        this.friendsService = friendsService;
        this.invitationService = invitationService;
    }

    @GetMapping
    public String profile(Model model, Principal principal) {
        return "redirect:/profile/" + principal.getName();
    }

    @GetMapping("/{userToFind}")
    public String otherUserProfile(Model model, @PathVariable String userToFind, Principal principal) {
        boolean selfProfile = userToFind.equals(principal.getName());
        User user = service.findByUsername(userToFind);

        model.addAttribute("friend", friendsService.isFriend(principal.getName(), userToFind));
        model.addAttribute("invitationSent", friendsService.isFriendInvitationSent(principal.getName(), userToFind));
        model.addAttribute("username", principal.getName());
        model.addAttribute("name", user.getUsername());
        model.addAttribute("gamesPlayed", user.getGamesPlayed());
        model.addAttribute("gamesWon", user.getGamesWon());
        model.addAttribute("movesDone", user.getMovesDone());
        model.addAttribute("selfProfile", selfProfile);
        return "player/profile";
    }
}
