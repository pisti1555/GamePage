package project.gamepage.web.controller.game.fly_in_the_web;

import project.gamepage.data.model.game.PvP;
import project.gamepage.data.model.game.spiderweb.Board;
import project.gamepage.data.model.user.User;
import project.gamepage.service.GameService;
import project.gamepage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/fly-in-the-web/api/game/pvp")
public class GamePvPAPIController {
    GameService service;
    UserService userService;
    SimpMessagingTemplate template;

    @Autowired
    public GamePvPAPIController(GameService service, UserService userService, SimpMessagingTemplate template) {
        this.service = service;
        this.userService = userService;
        this.template = template;
    }

    @PostMapping("/move")
    public int playVsPlayer(@RequestParam("from")int from, @RequestParam("to")int to, Principal principal) {
        PvP pvP = service.getPvP(principal.getName());
        Board board = findBoardPvP(principal.getName());

        service.moveVsPlayer(from, to, board, principal.getName());
        template.convertAndSendToUser(pvP.getUser1(), "/topic/game/update", "update");
        template.convertAndSendToUser(pvP.getUser2(), "/topic/game/update", "update");

        return service.whoWon(board);
    }

    @GetMapping("/getPositions")
    public int[] sendPositionsToClient(Principal principal) {
        Board board = findBoardPvP(principal.getName());
        return service.getPositions(board);
    }

    @GetMapping("/getConnections")
    public HashMap<Integer, ArrayList<Integer>> getConnections(Principal principal) {
        Board board = findBoardPvP(principal.getName());
        return service.getConnections(board);
    }

    @GetMapping("/getGameMode")
    public short getGameMode(Principal principal) {
        Board board = findBoardPvP(principal.getName());
        return service.getGameMode(board);
    }

    @GetMapping("/isFlysTurn")
    public boolean isFlysTurn(Principal principal) {
        Board board = findBoardPvP(principal.getName());
        return service.isFlysTurn(board);
    }

    @PostMapping("/newGame")
    public void newGame(@RequestParam("mode") String mode, Principal principal) {
        service.newGamePvP(principal.getName());
    }


    @GetMapping("/game-over")
    public int gameOver(Principal principal) {
        PvP pvp = service.getPvP(principal.getName());
        return service.gameOver(pvp);
    }

    @GetMapping("/is-game-over")
    public boolean isGameOver(Principal principal) {
        PvP pvp = service.getPvP(principal.getName());
        return !pvp.getBoard().isGameRunning();
    }

    @GetMapping("/getFlyStepsDone")
    public int getStepsDone(Principal principal) {
        Board board = findBoardPvP(principal.getName());
        return service.getFlyStepsDone(board);
    }

    @GetMapping("/getSpiderStepsDone")
    public int getSpiderStepsDone(Principal principal) {
        Board board = findBoardPvP(principal.getName());
        return service.getSpiderStepsDone(board);
    }

    private Board findBoardPvP(String name) {
        return service.getPvP(name).getBoard();
    }
}