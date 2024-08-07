package project.gamepage.web.controller.game.fly_in_the_web;

import project.gamepage.data.model.game.fly_in_the_web.FITW;
import project.gamepage.service.game.GameService_FITW;
import project.gamepage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/fly-in-the-web/api/game/pvc")
public class PvCAPIController_FITW {
    GameService_FITW service;
    UserService userService;

    @Autowired
    public PvCAPIController_FITW(GameService_FITW service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @PostMapping("/pvs")
    public int playVsSpider(@RequestParam("from")int from, @RequestParam("to")int to, Principal principal) {
        FITW boardFITW = service.getPvC(principal.getName()).getBoard();
        service.moveVsComputer(from, to, boardFITW);
        service.randomMoveSpider(boardFITW);
        return service.whoWon(boardFITW);
    }

    @PostMapping("/pvf")
    public int playVsFly(@RequestParam("from")int from, @RequestParam("to")int to, Principal principal) {
        FITW boardFITW = service.getPvC(principal.getName()).getBoard();
        service.moveVsComputer(from, to, boardFITW);
        service.randomMoveFly(boardFITW);
        return service.whoWon(boardFITW);
    }

    @PostMapping("/get-positions")
    public int[] sendPositionsToClient(Principal principal) {
        FITW boardFITW = findBoardPvC(principal.getName());
        return service.getPositions(boardFITW);
    }

    @GetMapping("/get-connections")
    public HashMap<Integer, ArrayList<Integer>> getConnections(Principal principal) {
        FITW boardFITW = findBoardPvC(principal.getName());
        return service.getConnections(boardFITW);
    }

    @GetMapping("/get-available-fields")
    public ArrayList<Integer> getAvailableFields(@RequestParam("from") int from, Principal principal) {
        FITW boardFITW = findBoardPvC(principal.getName());
        ArrayList<Integer> available = new ArrayList<>();
        for (int i = 0; i < boardFITW.getField()[from].getConnections().size(); i++) {
            available.add(boardFITW.getField()[from].getConnections().get(i).getNumber());
        }
        return available;
    }

    @GetMapping("/get-game-mode")
    public short getGameMode(Principal principal) {
        FITW boardFITW = findBoardPvC(principal.getName());
        return service.getGameMode(boardFITW);
    }

    @GetMapping("/is-flys-turn")
    public boolean isFlysTurn(Principal principal) {
        FITW boardFITW = findBoardPvC(principal.getName());
        return service.isFlysTurn(boardFITW);
    }

    @PostMapping("/new-game")
    public void newGame(@RequestParam("mode") String mode, Principal principal) {
        service.newGamePvC(mode, principal.getName());
    }


    @GetMapping("/is-game-running")
    public boolean isGameRunning(Principal principal) {
        FITW boardFITW = findBoardPvC(principal.getName());
        return service.getIsGameRunning(boardFITW);
    }

    @GetMapping("/get-fly-steps-done")
    public int getStepsDone(Principal principal) {
        FITW boardFITW = findBoardPvC(principal.getName());
        return service.getFlyStepsDone(boardFITW);
    }

    @GetMapping("/get-spider-steps-done")
    public int getSpiderStepsDone(Principal principal) {
        FITW boardFITW = findBoardPvC(principal.getName());
        return service.getSpiderStepsDone(boardFITW);
    }

    private FITW findBoardPvC(String name) {
        return service.getPvC(name).getBoard();
    }
}
