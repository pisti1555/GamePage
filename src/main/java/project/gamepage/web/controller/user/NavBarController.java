package project.gamepage.web.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.gamepage.service.invitations.GameInvitation;
import project.gamepage.service.invitations.InvitationService;
import project.gamepage.service.UserFriendsService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/nav-bar")
public class NavBarController {
    private final UserFriendsService friendsService;
    private final InvitationService invitationService;

    @Autowired
    public NavBarController(UserFriendsService friendsService, InvitationService invitationService) {
        this.friendsService = friendsService;
        this.invitationService = invitationService;
    }

    @GetMapping("/friend-invites")
    public List<String> getFriendInvites(Principal principal) {
        return friendsService.getFriendRequests(principal.getName());
    }

    @GetMapping("/friend-invites-count")
    public int getFriendInvitesCount(Principal principal) {
        return friendsService.getFriendRequestCount(principal.getName());
    }

    @GetMapping("/game-invites")
    public List<GameInvitation> getGameInvites(Principal principal) {
        return invitationService.getInvites(principal.getName());
    }

    @GetMapping("/game-invites-count")
    public int getGameInvitesCount(Principal principal) {
        return invitationService.invCount(principal.getName());
    }
}