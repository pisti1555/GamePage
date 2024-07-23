package project.gamepage.service.invitations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.gamepage.service.UserService;
import project.gamepage.web.dto.ProfileDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvitationService {
    private final UserService userService;
    public Map<String, List<GameInvitation>> invites;

    @Autowired
    public InvitationService(UserService userService) {
        this.userService = userService;
        this.invites = new HashMap<>();
    }

    public void inviteFriend(String inviter, String invited, String game) {
        List<GameInvitation> invitations = getInvites(invited);
        ProfileDto dto = new ProfileDto(userService.findByUsername(inviter));
        invitations.add(new GameInvitation(dto, game));
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
                invitations.removeIf(invitation -> invitation.getInviter().getUsername().equals(inver) && invitation.getGame().equals(game));
            }
        }
    }

    public boolean isInvitationSent(String username, String inver, String game) {
        for (Map.Entry<String, List<GameInvitation>> entry : invites.entrySet()) {
            String invited = entry.getKey();
            if (invited.equals(username)) {
                List<GameInvitation> invitations = entry.getValue();
                for (GameInvitation invitation : invitations) {
                    if (invitation.getInviter().getUsername().equals(inver) && invitation.getGame().equals(game)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
