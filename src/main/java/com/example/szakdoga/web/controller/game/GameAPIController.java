package com.example.szakdoga.web.controller.game;

import com.example.szakdoga.data.model.game.spiderweb.Board;
import com.example.szakdoga.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/game")
public class GameAPIController {
    GameService service;
    SimpMessagingTemplate template;

    @Autowired
    public GameAPIController(GameService service, SimpMessagingTemplate template) {
        this.service = service;
        this.template = template;
    }

    @PostMapping("/playVsPlayer")
    public int playVsPlayer(@RequestParam("from")int from, @RequestParam("to")int to, Principal principal) {
        Board board = findBoard(principal.getName());
        if (service.isMoveValid(board, from, to)) {
            service.move(board, from, to);
        } else {
            System.out.println("Invalid move");
        }

        return service.whoWon(board);
    }

    @PostMapping("/playVsSpider")
    public int playVsSpider(@RequestParam("from")int from, @RequestParam("to")int to, Principal principal) {
        Board board = service.getGame(principal.getName()).getBoard();
        if (service.isMoveValid(board, from, to)) {
            service.move(board, from, to);

            service.randomMoveSpider(board);
        } else {
            System.out.println("Invalid move");
        }

        if (service.whoWon(board) == 1) {

        } else if (service.whoWon(board) == 2) {

        }

        return service.whoWon(board);
    }

    @PostMapping("/playVsFly")
    public int playVsFly(@RequestParam("from")int from, @RequestParam("to")int to, Principal principal) {
        Board board = service.getGame(principal.getName()).getBoard();
        if (service.isMoveValid(board, from, to)) {
            service.move(board, from, to);
            service.randomMoveFly(board);
        } else {
            System.out.println("Invalid move");
        }

        if (service.whoWon(board) == 2) {

        } else if (service.whoWon(board) == 1) {
        }

        return service.whoWon(board);
    }

    @PostMapping("/getPositions")
    public int[] sendPositionsToClient(Principal principal) {
        Board board = findBoard(principal.getName());
        return service.getPositions(board);
    }

    @GetMapping("/getConnections")
    public HashMap<Integer, ArrayList<Integer>> getConnections(Principal principal) {
        Board board = findBoard(principal.getName());
        return service.getConnections(board);
    }

    @GetMapping("/getGameMode")
    public short getGameMode(Principal principal) {
        Board board = findBoard(principal.getName());
        return service.getGameMode(board);
    }

    @GetMapping("/isFlysTurn")
    public boolean isFlysTurn(Principal principal) {
        Board board = findBoard(principal.getName());
        return service.isFlysTurn(board);
    }

    @PostMapping("/newGame")
    public void newGame(@RequestParam("mode") String mode, Principal principal) {
        Board board = findBoard(principal.getName());
        service.newGame(board, mode);
    }


    @GetMapping("/isGameRunning")
    public boolean isGameRunning(Principal principal) {
        Board board = findBoard(principal.getName());
        return service.getIsGameRunning(board);
    }

    @GetMapping("/getFlyStepsDone")
    public int getStepsDone(Principal principal) {
        Board board = findBoard(principal.getName());
        return service.getFlyStepsDone(board);
    }

    @GetMapping("/getSpiderStepsDone")
    public int getSpiderStepsDone(Principal principal) {
        Board board = findBoard(principal.getName());
        return service.getSpiderStepsDone(board);
    }

    private Board findBoard(String name) {
        return service.getGame(name).getBoard();
    }
}
