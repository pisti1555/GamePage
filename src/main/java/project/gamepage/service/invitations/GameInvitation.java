package project.gamepage.service.invitations;

import project.gamepage.web.dto.ProfileDto;

public class GameInvitation {
    private ProfileDto inviter;
    private String game;

    public GameInvitation(ProfileDto dto, String game) {
        this.inviter = dto;
        this.game = game;
    }


    public String getGame() {
        return game;
    }

    public ProfileDto getInviter() {
        return inviter;
    }
}
