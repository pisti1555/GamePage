package project.gamepage.web.controller.game;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.gamepage.data.model.game.PvP;
import project.gamepage.data.model.game.fly_in_the_web.FITW;
import project.gamepage.data.model.game.tic_tac_toe.TicTacToe;
import project.gamepage.service.UserFriendsService;
import project.gamepage.service.game.fly_in_the_web.GameService_FITW;
import project.gamepage.service.game.tic_tac_toe.GameService_TicTacToe;
import project.gamepage.service.invitations.InvitationService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/lobby")
public class APILobbyController {
    private final UserFriendsService friendsService;
    private final InvitationService invitationService;
    private final GameService_TicTacToe ticTacToeService;
    private final GameService_FITW fitwService;
    @Autowired
    public APILobbyController(UserFriendsService friendsService, InvitationService invitationService, GameService_TicTacToe ticTacToeService, GameService_FITW fitwService) {
        this.friendsService = friendsService;
        this.invitationService = invitationService;
        this.ticTacToeService = ticTacToeService;
        this.fitwService = fitwService;
    }

    @GetMapping("/already-invited-friend-list")
    private List<String> getAlreadyInvitedFriendList(Principal principal, @RequestParam("game")String game) {
        String username = principal.getName();
        List<String> friendList = friendsService.getUserFriendsList(username);
        ArrayList<String> invitedList = new ArrayList<>();
        for (String user : friendList) {
            if(invitationService.isInvitationSent(user, username, game)) {
                invitedList.add(user);
            }
        }
        return invitedList;
    }

    @GetMapping("/uninvited-friend-list")
    private List<String> getUninvitedFriendList(Principal principal, @RequestParam("game")String game) {
        String username = principal.getName();
        List<String> friendList = friendsService.getUserFriendsList(username);
        ArrayList<String> uninvitedList = new ArrayList<>();
        for (String user : friendList) {
            if(!invitationService.isInvitationSent(user, username, game)) {
                uninvitedList.add(user);
            }
        }
        return uninvitedList;
    }

    @GetMapping("/get-lobby-users")
    private String[] getLobbyUsers(Principal principal, @RequestParam("game")String game) {
        String username = principal.getName();
        if (game.equals("TicTacToe")) {
            PvP<TicTacToe> pvp = ticTacToeService.getPvP(username);
            String[] users = new String[2];
            users[0] = pvp.getUser1();
            users[1] = pvp.getUser2();
            return users;
        }
        if (game.equals("FITW")) {
            PvP<FITW> pvp = fitwService.getPvP(username);
            String[] users = new String[2];
            users[0] = pvp.getUser1();
            users[1] = pvp.getUser2();
            return users;
        }
        return null;
    }

    @GetMapping("/get-username")
    private String getUsername(Principal principal) {
        return principal.getName();
    }

    @GetMapping("is-player1-ready")
    private boolean isPlayer1Ready(Principal principal, @RequestParam("game")String game) {
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
    private boolean isPlayer2Ready(Principal principal, @RequestParam("game")String game) {
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
    private void ready(Principal principal, @RequestParam("game")String game) {
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
    private boolean isAllReady(Principal principal, @RequestParam("game")String game) {
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
    public void declineLobbyInvitation_TicTacToe(HttpServletRequest http, Principal principal, @RequestParam("inviter")String inviter, @RequestParam("game")String game) {
        invitationService.removeInvitation(principal.getName(), inviter, game);
    }
}