package project.gamepage.web.controller.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import project.gamepage.data.model.game.PvP;
import project.gamepage.data.model.game.fly_in_the_web.FITW;
import project.gamepage.data.model.game.tic_tac_toe.TicTacToe;
import project.gamepage.service.ProfileDataService;
import project.gamepage.service.UserFriendsService;
import project.gamepage.service.UserService;
import project.gamepage.service.game.fly_in_the_web.GameService_FITW;
import project.gamepage.service.game.tic_tac_toe.GameService_TicTacToe;
import project.gamepage.service.invitations.InvitationService;
import project.gamepage.web.dto.ProfileDto;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/lobby")
public class APILobbyController {
    private final UserFriendsService friendsService;
    private final ProfileDataService profileDataService;
    private final UserService userService;
    private final InvitationService invitationService;
    private final GameService_TicTacToe ticTacToeService;
    private final GameService_FITW fitwService;
    private final SimpMessagingTemplate template;
    @Autowired
    public APILobbyController(UserFriendsService friendsService, ProfileDataService profileDataService, UserService userService, InvitationService invitationService, GameService_TicTacToe ticTacToeService, GameService_FITW fitwService, SimpMessagingTemplate template) {
        this.friendsService = friendsService;
        this.profileDataService = profileDataService;
        this.userService = userService;
        this.invitationService = invitationService;
        this.ticTacToeService = ticTacToeService;
        this.fitwService = fitwService;
        this.template = template;
    }

    @GetMapping("/already-invited-friend-list")
    public List<ProfileDto> getAlreadyInvitedFriendList(Principal principal, @RequestParam("game")String game) {
        String username = principal.getName();
        List<ProfileDto> friendList = friendsService.getUserFriendsList(username);
        ArrayList<ProfileDto> invitedList = new ArrayList<>();
        for (ProfileDto user : friendList) {
            if(invitationService.isInvitationSent(user.getUsername(), username, game)) {
                invitedList.add(user);
            }
        }
        return invitedList;
    }

    @GetMapping("/uninvited-friend-list")
    public List<ProfileDto> getUninvitedFriendList(Principal principal, @RequestParam("game")String game) {
        String username = principal.getName();
        List<ProfileDto> friendList = friendsService.getUserFriendsList(username);
        ArrayList<ProfileDto> uninvitedList = new ArrayList<>();
        for (ProfileDto user : friendList) {
            if(!invitationService.isInvitationSent(user.getUsername(), username, game)) {
                uninvitedList.add(user);
            }
        }

        return uninvitedList;
    }

    @GetMapping("/get-lobby-users")
    public ProfileDto[] getLobbyUsers(Principal principal, @RequestParam("game")String game) {
        String username = principal.getName();
        if (game.equals("TicTacToe")) {
            PvP<TicTacToe> pvp = ticTacToeService.getPvP(username);
            ProfileDto[] users = new ProfileDto[2];
            users[0] = new ProfileDto(userService.findByUsername(pvp.getUser1()));
            if (pvp.getUser2() != null) users[1] = new ProfileDto(userService.findByUsername(pvp.getUser2()));
            return users;
        }
        if (game.equals("FITW")) {
            PvP<FITW> pvp = fitwService.getPvP(username);
            ProfileDto[] users = new ProfileDto[2];
            users[0] = profileDataService.getProfileData(pvp.getUser1());
            if (pvp.getUser2() != null) users[1] = new ProfileDto(userService.findByUsername(pvp.getUser2()));
            return users;
        }
        return null;
    }

    @GetMapping("/get-username")
    public String getUsername(Principal principal) {
        return principal.getName();
    }

    @GetMapping("is-player1-ready")
    public boolean isPlayer1Ready(Principal principal, @RequestParam("game")String game) {
        String username = principal.getName();
        if (game.equals("TicTacToe")) {
            PvP<TicTacToe> pvp = ticTacToeService.getPvP(username);
            return pvp.isUser1Ready();
        }
        if (game.equals("FITW")) {
            PvP<FITW> pvp = fitwService.getPvP(username);
            return pvp.isUser1Ready();
        }
        return false;
    }

    @GetMapping("is-player2-ready")
    public boolean isPlayer2Ready(Principal principal, @RequestParam("game")String game) {
        String username = principal.getName();
        if (game.equals("TicTacToe")) {
            PvP<TicTacToe> pvp = ticTacToeService.getPvP(username);
            return pvp.isUser2Ready();
        }
        if (game.equals("FITW")) {
            PvP<FITW> pvp = fitwService.getPvP(username);
            return pvp.isUser2Ready();
        }
        return false;
    }

    @GetMapping("set-ready")
    public void ready(Principal principal, @RequestParam("game")String game) {
        String username = principal.getName();
        if (game.equals("TicTacToe")) {
            PvP<TicTacToe> pvp = ticTacToeService.getPvP(username);
            if (username.equals(pvp.getUser2())) {
                pvp.setUser2Ready(!pvp.isUser2Ready());
            } else if (username.equals(pvp.getUser1())) {
                pvp.setUser1Ready(!pvp.isUser1Ready());
            }
        }
        if (game.equals("FITW")) {
            PvP<FITW> pvp = fitwService.getPvP(username);
            if (username.equals(pvp.getUser2())) {
                pvp.setUser2Ready(!pvp.isUser2Ready());
            } else if (username.equals(pvp.getUser1())) {
                pvp.setUser1Ready(!pvp.isUser1Ready());
            }
        }
    }

    @GetMapping("/is-all-ready")
    public boolean isAllReady(Principal principal, @RequestParam("game")String game) {
        if (game.equals("TicTacToe")) {
            PvP<TicTacToe> pvp = ticTacToeService.getPvP(principal.getName());
            return pvp.isReadyToStart();
        }
        if (game.equals("FITW")) {
            PvP<FITW> pvp = fitwService.getPvP(principal.getName());
            return pvp.isReadyToStart();
        }
        return false;
    }

    @GetMapping("/decline-lobby-invitation")
    public ResponseEntity<Boolean> declineLobbyInvitation_TicTacToe(Principal principal, @RequestParam("inviter")String inviter, @RequestParam("game")String game) {
        invitationService.removeInvitation(principal.getName(), inviter, game);
        template.convertAndSendToUser(inviter, "/topic/lobby/update", "update");
        return ResponseEntity.ok(true);
    }
}