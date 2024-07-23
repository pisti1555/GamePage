package project.gamepage.web.controller.game.fly_in_the_web;

import project.gamepage.data.model.game.PvP;
import project.gamepage.data.model.game.fly_in_the_web.FITW;
import project.gamepage.service.game.GameService_FITW;
import project.gamepage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/fly-in-the-web/api/game/pvp")
public class PvPAPIController_FITW {
    GameService_FITW service;
    UserService userService;
    SimpMessagingTemplate template;

    @Autowired
    public PvPAPIController_FITW(GameService_FITW service, UserService userService, SimpMessagingTemplate template) {
        this.service = service;
        this.userService = userService;
        this.template = template;
    }

    @GetMapping("/is-game-in-progress")
    public boolean isOver(Principal principal) {
        PvP<FITW> pvp = service.getPvP(principal.getName());
        return pvp.isInProgress();
    }

    @PostMapping("/move")
    public int playVsPlayer(@RequestParam("from")int from, @RequestParam("to")int to, Principal principal) {
        PvP<FITW> pvp = service.getPvP(principal.getName());
        FITW boardFITW = findBoardPvP(principal.getName());

        service.moveVsPlayer(from, to, boardFITW, principal.getName());
        template.convertAndSendToUser(pvp.getUser1(), "/topic/game/update", "update");
        template.convertAndSendToUser(pvp.getUser2(), "/topic/game/update", "update");

        return service.whoWon(boardFITW);
    }

    @GetMapping("/get-positions")
    public int[] sendPositionsToClient(Principal principal) {
        FITW boardFITW = findBoardPvP(principal.getName());
        return service.getPositions(boardFITW);
    }

    @GetMapping("/get-connections")
    public HashMap<Integer, ArrayList<Integer>> getConnections(Principal principal) {
        FITW boardFITW = findBoardPvP(principal.getName());
        return service.getConnections(boardFITW);
    }

    @GetMapping("/get-game-mode")
    public short getGameMode(Principal principal) {
        FITW boardFITW = findBoardPvP(principal.getName());
        return service.getGameMode(boardFITW);
    }

    @GetMapping("/is-flys-turn")
    public boolean isFlysTurn(Principal principal) {
        FITW boardFITW = findBoardPvP(principal.getName());
        return service.isFlysTurn(boardFITW);
    }

    @PostMapping("/new-game")
    public void newGame(@RequestParam("mode") String mode, Principal principal) {
        service.newGamePvP(principal.getName());
    }


    @GetMapping("/game-over")
    public int gameOver(Principal principal) {
        PvP<FITW> pvp = service.getPvP(principal.getName());
        return service.gameOver(pvp);
    }

    @GetMapping("/is-game-over")
    public boolean isGameOver(Principal principal) {
        PvP<FITW> pvp = service.getPvP(principal.getName());
        return !pvp.getBoard().isGameRunning();
    }

    @GetMapping("/get-fly-steps-done")
    public int getStepsDone(Principal principal) {
        FITW boardFITW = findBoardPvP(principal.getName());
        return service.getFlyStepsDone(boardFITW);
    }

    @GetMapping("/get-spider-steps-done")
    public int getSpiderStepsDone(Principal principal) {
        FITW boardFITW = findBoardPvP(principal.getName());
        return service.getSpiderStepsDone(boardFITW);
    }

    private FITW findBoardPvP(String name) {
        return service.getPvP(name).getBoard();
    }
}