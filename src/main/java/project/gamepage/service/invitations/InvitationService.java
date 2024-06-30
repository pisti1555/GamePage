package project.gamepage.service.invitations;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvitationService {
    public Map<String, GameInvitation> invites;

    public InvitationService() {
        this.invites = new HashMap<>();
    }

    public void inviteFriend(String inviter, String invited, String game) {
        invites.put(invited, new GameInvitation(inviter, game));
    }

    public List<GameInvitation> getInvites(String username) {
        List<GameInvitation> invitations = new ArrayList<>();

        for (Map.Entry<String, GameInvitation> entry : invites.entrySet()) {
            String invited = entry.getKey();
            GameInvitation invitation = entry.getValue();
            if (invited.equals(username)) {
                invitations.add(invitation);
            }
        }

        return invitations;
    }

    public int invCount(String username) {
        return getInvites(username).size();
    }

    public void declineInvite(String username, String inver, String game) {
        for (Map.Entry<String, GameInvitation> entry : invites.entrySet()) {
            String invited = entry.getKey();
            String inviter = entry.getValue().getInviter();
            String gameName = entry.getValue().getGame();
            if (invited.equals(username) && inviter.equals(inver) && gameName.equals(game)) {
                invites.remove(entry.getKey());
            }
        }
    }

}
