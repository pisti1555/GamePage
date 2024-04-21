package com.example.szakdoga.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvitationService {
    Map<String, String> invites;

    public InvitationService() {
        this.invites = new HashMap<>();
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

    public int invCount(String username) {
        return getInvites(username).size();
    }
}
