package project.gamepage.web.controller.user;

import project.gamepage.data.model.user.User;
import project.gamepage.service.InvitationService;
import project.gamepage.service.UserFriendsService;
import project.gamepage.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/friends")
public class FriendController {
    UserFriendsService service;
    UserService userService;
    InvitationService invitationService;
    SimpMessagingTemplate template;
    @Autowired
    public FriendController(UserFriendsService service, UserService userService, InvitationService invitationService, SimpMessagingTemplate template) {
        this.service = service;
        this.userService = userService;
        this.invitationService = invitationService;
        this.template = template;
    }


    @GetMapping
    public String getFriendList(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        List<String> friendList = service.getUserFriendsList(principal.getName());

        if (friendList != null) {
            model.addAttribute("success", true);
            model.addAttribute("friends", friendList);
            model.addAttribute("count", friendList.size());
        }

        model.addAttribute("invites", invitationService.getInvites(principal.getName()));
        model.addAttribute("invCount", invitationService.invCount(principal.getName()));

        model.addAttribute("friendRequests", service.getFriendRequests(principal.getName()));
        model.addAttribute("friendRequestCount", service.getFriendRequestCount(principal.getName()));

        return "player/friendList";
    }

    @GetMapping("/add")
    public String getAddFriend(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());

        model.addAttribute("invites", invitationService.getInvites(principal.getName()));
        model.addAttribute("invCount", invitationService.invCount(principal.getName()));

        model.addAttribute("friendRequests", service.getFriendRequests(principal.getName()));
        model.addAttribute("friendRequestCount", service.getFriendRequestCount(principal.getName()));

        return "player/addFriend";
    }

    @PostMapping("/add")
    public String addFriend(Model model, Principal principal, String invited, HttpServletRequest http) {
        User invitedUser = userService.findByUsername(invited);
        if (invitedUser == null) {
            return "redirect:/friends/add?userNotFound";
        }

        service.addUserFriends(principal.getName(), invited);
        template.convertAndSendToUser(invited, "/topic/invites", "friend-request");
        return "redirect:" + http.getHeader("Referer");
    }


    @PostMapping("/decline-friend-request")
    public String declineFriendRequest(Model model, Principal principal, String inviter, HttpServletRequest http) {
        service.declineFriendRequest(principal.getName(), inviter);
        return "redirect:" + http.getHeader("Referer");
    }

    @PostMapping("/delete-friend")
    public String deleteFriend(Model model, Principal principal, String user2, HttpServletRequest http) {
        service.deleteFriends(principal.getName(), user2);
        return "redirect:" + http.getHeader("Referer");
    }
}