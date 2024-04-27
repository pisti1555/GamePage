package com.example.szakdoga.web.controller.game;

import com.example.szakdoga.data.model.game.spiderweb.Board;
import com.example.szakdoga.service.GameService;
import com.example.szakdoga.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/game/pvc")
public class GamePvCAPIController {
    GameService service;
    UserService userService;

    @Autowired
    public GamePvCAPIController(GameService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @PostMapping("/pvs")
    public int playVsSpider(@RequestParam("from")int from, @RequestParam("to")int to, Principal principal) {
        Board board = service.getPvC(principal.getName()).getBoard();
        service.moveVsComputer(from, to, board);
        service.randomMoveSpider(board);
        return service.whoWon(board);
    }

    @PostMapping("/pvf")
    public int playVsFly(@RequestParam("from")int from, @RequestParam("to")int to, Principal principal) {
        Board board = service.getPvC(principal.getName()).getBoard();
        service.moveVsComputer(from, to, board);
        service.randomMoveFly(board);
        return service.whoWon(board);
    }

    @PostMapping("/getPositions")
    public int[] sendPositionsToClient(Principal principal) {
        Board board = findBoardPvC(principal.getName());
        return service.getPositions(board);
    }

    @GetMapping("/getConnections")
    public HashMap<Integer, ArrayList<Integer>> getConnections(Principal principal) {
        Board board = findBoardPvC(principal.getName());
        return service.getConnections(board);
    }

    @GetMapping("/getGameMode")
    public short getGameMode(Principal principal) {
        Board board = findBoardPvC(principal.getName());
        return service.getGameMode(board);
    }

    @GetMapping("/isFlysTurn")
    public boolean isFlysTurn(Principal principal) {
        Board board = findBoardPvC(principal.getName());
        return service.isFlysTurn(board);
    }

    @PostMapping("/newGame")
    public void newGame(@RequestParam("mode") String mode, Principal principal) {
        service.newGamePvC(mode, principal.getName());
    }


    @GetMapping("/isGameRunning")
    public boolean isGameRunning(Principal principal) {
        Board board = findBoardPvC(principal.getName());
        return service.getIsGameRunning(board);
    }

    @GetMapping("/getFlyStepsDone")
    public int getStepsDone(Principal principal) {
        Board board = findBoardPvC(principal.getName());
        return service.getFlyStepsDone(board);
    }

    @GetMapping("/getSpiderStepsDone")
    public int getSpiderStepsDone(Principal principal) {
        Board board = findBoardPvC(principal.getName());
        return service.getSpiderStepsDone(board);
    }

    private Board findBoardPvC(String name) {
        return service.getPvC(name).getBoard();
    }
}
