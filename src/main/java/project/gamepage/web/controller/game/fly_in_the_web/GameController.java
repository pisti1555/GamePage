package project.gamepage.web.controller.game.fly_in_the_web;

import project.gamepage.data.model.user.User;
import project.gamepage.data.model.game.PvP;
import project.gamepage.service.GameService;
import project.gamepage.service.InvitationService;
import project.gamepage.service.UserFriendsService;
import project.gamepage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/fly-in-the-web/game")
public class GameController {
    GameService service;
    UserFriendsService friendsService;
    SimpMessagingTemplate template;
    InvitationService invitationService;

    @Autowired
    public GameController(GameService service, UserFriendsService friendsService, SimpMessagingTemplate template, InvitationService invitationService) {
        this.service = service;
        this.friendsService = friendsService;
        this.template = template;
        this.invitationService = invitationService;
    }

    @GetMapping
    public String getGamePage(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "game/fly_in_the_web/gameMenu";
    }

    @GetMapping("/pvp")
    public String pvp(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);
        PvP pvP = service.getPvP(username);
        if (pvP.getUser2() != null) {
            if (pvP.isReadyToStart()) {
                pvP.setUser1InGame(true);
                pvP.setUser2InGame(true);
                pvP.setDbUpdated(false);
            }

            return "game/fly_in_the_web/spiderweb_pvp";
        }

        return "redirect:/fly-in-the-web/lobby?error=noGameFound";
    }

    @GetMapping("/pvs")
    public String pvs(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        service.newGamePvC("pvs", principal.getName());
        return "game/fly_in_the_web/spiderweb_pvs";
    }

    @GetMapping("/pvf")
    public String pvf(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        service.newGamePvC("pvf", principal.getName());
        return "game/fly_in_the_web/spiderweb_pvf";
    }

    @GetMapping("/return-to-lobby")
    public String returnToLobby(Principal principal) {
        PvP pvP = service.getPvP(principal.getName());
        if (principal.getName().equals(pvP.getUser1())) {
            pvP.setUser1InGame(false);
            template.convertAndSendToUser(pvP.getUser2(), "/topic/game/update", "return");
            template.convertAndSendToUser(pvP.getUser2(), "/topic/lobby/update", "return");
        } else {
            pvP.setUser2InGame(false);
            template.convertAndSendToUser(pvP.getUser1(), "/topic/game/update", "return");
            template.convertAndSendToUser(pvP.getUser1(), "/topic/lobby/update", "return");
        }

        if (!pvP.isUser1InGame() && !pvP.isUser2InGame()) {
            //TODO
        }

        return "redirect:/fly-in-the-web/lobby";
    }
}