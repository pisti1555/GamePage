package com.example.szakdoga.service;

import com.example.szakdoga.data.model.game.PvP;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    List<PvP> gameList = new ArrayList<>();
    Map<String, String> invites = new ConcurrentHashMap<>();

    public PvP getGame(String username) {
        for (PvP game : gameList) {
            if (game.getUser1().equals(username) || (game.getUser2() != null && game.getUser2().equals(username))) {
                return game;
            }
        }

        PvP newGame = new PvP(username, null, username);
        gameList.add(newGame);
        return newGame;
    }

    public PvP joinLobby(String inviter, String invited) {
        for (PvP game : gameList) {
            if (game.getUser1().equals(inviter)) {
                gameList.removeIf(i -> i.getUser1().equals(invited));
                game.setUser2(invited);

                for (Map.Entry<String, String> entry : invites.entrySet()) {
                    String invitedUser = entry.getKey();
                    String inviterUser = entry.getValue();
                    if (invited.equals(invitedUser)  && inviter.equals(inviterUser)) {
                        invites.remove(invitedUser);
                    }
                }

                return game;
            }
        }
        return null;
    }

    public void inviteFriend(String inviter, String invited) {
        invites.put(invited, inviter);
    }

    public List<String> getInvites(String username) {
        List<String> invitations = new ArrayList<>();

        for (Map.Entry<String, String> entry : invites.entrySet()) {
            String invited = entry.getKey();
            String inviter = entry.getValue();
            if (invited.equals(username)) {
                invitations.add(inviter);
            }
        }

        return invitations;
    }

    public void changeString(String username, String string) {
        for (PvP i : gameList) {
            if (i.getUser1().equals(username) || i.getUser2().equals(username)) {
                i.setGame(string);
                System.out.println("megváltozott");
            }
        }
    }
}