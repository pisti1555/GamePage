package project.gamepage.web.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import project.gamepage.data.model.user.User;
import project.gamepage.service.invitations.InvitationService;
import project.gamepage.service.UserFriendsService;
import project.gamepage.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.gamepage.web.dto.ProfileDto;

import java.security.Principal;

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
        ProfileDto dto = new ProfileDto(userService.findByUsername(principal.getName()));
        model.addAttribute("dto", dto);
        return "player/friendList";
    }

    @GetMapping("/add")
    private boolean addFriend(Model model, Principal principal, @RequestParam("invited")String invited) {
        User invitedUser = userService.findByUsername(invited);
        if (invitedUser == null) return false;
        service.addUserFriends(principal.getName(), invited);
        template.convertAndSendToUser(invited, "/topic/invites", "friend-request");
        return true;
    }

    @GetMapping("/is-friend")
    private ResponseEntity<Boolean> isFriend(Principal principal, @RequestParam("user")String user) {
        boolean result = service.isFriend(principal.getName(), user);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/is-friend-request-sent")
    private ResponseEntity<Boolean> isFriendRequestSent(Principal principal, @RequestParam("user")String user) {
        boolean result = service.isFriendInvitationSent(principal.getName(), user);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/decline-friend-request")
    public ResponseEntity<Boolean> declineFriendRequest(Principal principal, @RequestParam("inviter")String inviter) {
        service.declineFriendRequest(principal.getName(), inviter);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/delete-friend")
    public ResponseEntity<Boolean> deleteFriend(Principal principal, @RequestParam("user2")String user2) {
        service.deleteFriends(principal.getName(), user2);
        return ResponseEntity.ok(true);
    }
}