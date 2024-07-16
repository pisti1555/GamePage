package project.gamepage.web.controller.user;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import project.gamepage.data.model.user.User;
import project.gamepage.service.invitations.InvitationService;
import project.gamepage.service.UserFriendsService;
import project.gamepage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import project.gamepage.web.dto.ProfileDto;
import project.gamepage.web.dto.UserDto;

import java.security.Principal;

@Controller
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

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        return "redirect:/profile/" + principal.getName();
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("user", service.findByUsername(principal.getName()));
        return "player/profile_edit";
    }

    @PostMapping("/profile/update")
    public String editProfilePost(Principal principal, @ModelAttribute("dto") UserDto dto) {
        User user = service.editProfile(service.findByUsername(principal.getName()), dto);
        if (user != null) return "redirect:/profile/edit?success";
        return "redirect:/profile/edit?error";
    }

    @GetMapping("/profile/{userToFind}")
    public String otherUserProfile(Model model, @PathVariable String userToFind, Principal principal) {
        ProfileDto dto = new ProfileDto(service.findByUsername(userToFind));
        model.addAttribute("username", principal.getName());
        model.addAttribute("profile", dto);
        return "player/profile";
    }
}
