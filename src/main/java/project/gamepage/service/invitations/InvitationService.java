package project.gamepage.service.invitations;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvitationService {
    public Map<String, List<GameInvitation>> invites;

    public InvitationService() {
        this.invites = new HashMap<>();
    }

    public void inviteFriend(String inviter, String invited, String game) {
        List<GameInvitation> invitations = getInvites(invited);
        invitations.add(new GameInvitation(inviter, game));
        invites.put(invited, invitations);
    }

    public List<GameInvitation> getInvites(String username) {
        for (Map.Entry<String, List<GameInvitation>> entry : invites.entrySet()) {
            if (entry.getKey().equals(username)) {
                return entry.getValue();
            }
        }
        return new ArrayList<>();
    }

    public int invCount(String username) {
        return getInvites(username).size();
    }

    public void removeInvitation(String username, String inver, String game) {
        for (Map.Entry<String, List<GameInvitation>> entry : invites.entrySet()) {
            String invited = entry.getKey();
            if (invited.equals(username)) {
                List<GameInvitation> invitations = entry.getValue();
                invitations.removeIf(invitation -> invitation.getInviter().equals(inver) && invitation.getGame().equals(game));
            }
        }
    }

}
