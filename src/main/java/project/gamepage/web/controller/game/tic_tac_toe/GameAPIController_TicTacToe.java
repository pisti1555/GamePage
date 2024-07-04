package project.gamepage.web.controller.game.tic_tac_toe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import project.gamepage.data.model.game.PvC;
import project.gamepage.data.model.game.PvP;
import project.gamepage.data.model.game.tic_tac_toe.Pieces_TicTacToe;
import project.gamepage.data.model.game.tic_tac_toe.TicTacToe;
import project.gamepage.service.game.tic_tac_toe.GameService_TicTacToe;

import java.security.Principal;

@RestController
@RequestMapping("/tic-tac-toe/api/game")
public class GameAPIController_TicTacToe {
    private final GameService_TicTacToe service;
    SimpMessagingTemplate template;
    @Autowired
    public GameAPIController_TicTacToe(GameService_TicTacToe service, SimpMessagingTemplate template) {
        this.service = service;
        this.template = template;
    }

    @GetMapping("/is-game-in-progress")
    public boolean isOver(Principal principal) {
        PvP<TicTacToe> pvp = service.getPvP(principal.getName());
        return pvp.isInProgress();
    }

    @PostMapping("/move")
    public void move(@RequestParam("row")int row, @RequestParam("col")int col, Principal principal) {
        PvP<TicTacToe> pvp = service.getPvP(principal.getName());
        TicTacToe game = service.getPvP(principal.getName()).getBoard();

        if (pvp.getUser1().equals(principal.getName())) {
            boolean isSomebodyWon = service.move(row, col, game, Pieces_TicTacToe.X);
            if (isSomebodyWon) pvp.setOver(true);
        }
        if (pvp.getUser2().equals(principal.getName())) {
            boolean isSomebodyWon = service.move(row, col, game, Pieces_TicTacToe.O);
            if (isSomebodyWon) pvp.setOver(true);
        }

        template.convertAndSendToUser(pvp.getUser1(), "/topic/game/update", "update");
        template.convertAndSendToUser(pvp.getUser2(), "/topic/game/update", "update");
    }

    @PostMapping("/move-pvc")
    public boolean movePvC(@RequestParam("row")int row, @RequestParam("col")int col, Principal principal) {
        PvC<TicTacToe> pvc = service.getPvC(principal.getName());
        TicTacToe game = service.getPvC(principal.getName()).getBoard();

        if (pvc.getUser().equals(principal.getName())) {
            return service.moveAI(row, col, game, Pieces_TicTacToe.X);
        }
        if (pvc.getUser2().equals(principal.getName())) {
            return service.moveAI(row, col, game, Pieces_TicTacToe.O);
        }

        return false;
    }

    @GetMapping("/which-won-pvp")
    public short whichWon_PvP(Principal principal) {
        TicTacToe game = service.getPvP(principal.getName()).getBoard();
        return service.whichWon(game);
    }

    @GetMapping("/which-won-pvc")
    public short whichWon_PvC(Principal principal) {
        TicTacToe game = service.getPvC(principal.getName()).getBoard();
        return service.whichWon(game);
    }

    @GetMapping("/get-positions-pvp")
    public int[][] getPositions_PvP(Principal principal) {
        PvP<TicTacToe> pvp = service.getPvP(principal.getName());
        int[][] pos = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (service.getPvP(principal.getName()).getBoard().getBoard()[i][j] == Pieces_TicTacToe.EMPTY) {
                    pos[i][j] = 0;
                }
                if (service.getPvP(principal.getName()).getBoard().getBoard()[i][j] == Pieces_TicTacToe.X) {
                    pos[i][j] = 1;
                }
                if (service.getPvP(principal.getName()).getBoard().getBoard()[i][j] == Pieces_TicTacToe.O) {
                    pos[i][j] = 2;
                }
            }
        }
        return pos;
    }

    @GetMapping("/get-positions-pvc")
    public int[][] getPositions_PvC(Principal principal) {
        int[][] pos = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (service.getPvC(principal.getName()).getBoard().getBoard()[i][j] == Pieces_TicTacToe.EMPTY) {
                    pos[i][j] = 0;
                }
                if (service.getPvC(principal.getName()).getBoard().getBoard()[i][j] == Pieces_TicTacToe.X) {
                    pos[i][j] = 1;
                }
                if (service.getPvC(principal.getName()).getBoard().getBoard()[i][j] == Pieces_TicTacToe.O) {
                    pos[i][j] = 2;
                }
            }
        }
        return pos;
    }

    @GetMapping("/new-game-pvc")
    public void newGamePvC(Principal principal) {
        PvC<TicTacToe> pvc = service.getPvC(principal.getName());
        pvc.setBoard(new TicTacToe());
    }

    @GetMapping("/get-lobby-users-pvc")
    public String[] getLobbyUsersPvC(Principal principal) {
        PvC<TicTacToe> pvc = service.getPvC(principal.getName());
        String[] users = new String[2];
        users[0] = pvc.getUser();
        users[1] = pvc.getUser2();
        return users;
    }

    @GetMapping("/get-lobby-users-pvp")
    public String[] getLobbyUsersPvP(Principal principal) {
        PvP<TicTacToe> pvp = service.getPvP(principal.getName());
        String[] users = new String[2];
        users[0] = pvp.getUser1();
        users[1] = pvp.getUser2();
        return users;
    }

}
