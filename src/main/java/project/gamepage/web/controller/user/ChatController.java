package project.gamepage.web.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import project.gamepage.data.model.chat.ChatMessage;
import project.gamepage.data.model.game.PvP;
import project.gamepage.data.model.game.fly_in_the_web.FITW;
import project.gamepage.data.model.game.tic_tac_toe.TicTacToe;
import project.gamepage.service.game.fly_in_the_web.GameService_FITW;
import project.gamepage.service.game.tic_tac_toe.GameService_TicTacToe;

import java.security.Principal;
import java.util.ArrayList;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final GameService_FITW service_fitw;
    private final GameService_TicTacToe service_ticTacToe;
    private final SimpMessagingTemplate template;
    @Autowired
    public ChatController(GameService_FITW service_fitw, GameService_TicTacToe service_ticTacToe, SimpMessagingTemplate template) {
        this.service_fitw = service_fitw;
        this.service_ticTacToe = service_ticTacToe;
        this.template = template;
    }

    @GetMapping("/get-lobby-chat")
    private ArrayList<ChatMessage> getLobbyChat(Principal principal, @RequestParam("game")String game) {
        if (game.equals("FITW")) return service_fitw.getPvP(principal.getName()).getChatMessages();
        if (game.equals("TicTacToe")) return service_ticTacToe.getPvP(principal.getName()).getChatMessages();
        return new ArrayList<>();
    }

    @PostMapping("/send-message-to-lobby")
    private boolean sendMessageToLobby(Principal principal, @RequestParam("message")String message, @RequestParam("game")String game) {
        if (message.isEmpty()) return false;
        if (game.equals("FITW")) {
            PvP<FITW> lobby = service_fitw.getPvP(principal.getName());
            lobby.sendMessage(new ChatMessage(principal.getName(), message));
            if (lobby.getUser1().equals(principal.getName())) {
                if (lobby.getUser2() != null) template.convertAndSendToUser(lobby.getUser2(), "/topic/chat/lobby", message);
            } else {
                template.convertAndSendToUser(lobby.getUser1(), "/topic/chat/lobby", message);
            }
            return true;
        }
        if (game.equals("TicTacToe")) {
            PvP<TicTacToe> lobby = service_ticTacToe.getPvP(principal.getName());
            lobby.sendMessage(new ChatMessage(principal.getName(), message));
            if (lobby.getUser1().equals(principal.getName())) {
                if (lobby.getUser2() != null) template.convertAndSendToUser(lobby.getUser2(), "/topic/chat/lobby", message);
            } else {
                template.convertAndSendToUser(lobby.getUser1(), "/topic/chat/lobby", message);
            }
            return true;
        }
        return false;
    }
}
