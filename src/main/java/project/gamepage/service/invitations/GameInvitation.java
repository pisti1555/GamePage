package project.gamepage.service.invitations;

public class GameInvitation {
    private String inviter;
    private String game;

    public GameInvitation(String inviter, String game) {
        this.inviter = inviter;
        this.game = game;
    }

    public String getInviter() {
        return inviter;
    }

    public String getGame() {
        return game;
    }
}
