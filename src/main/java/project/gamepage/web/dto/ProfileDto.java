package project.gamepage.web.dto;

import project.gamepage.data.model.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileDto {
    public String username;
    private String avatar;
    private String description;
    private Map<String, String> friendss;
    private List<ProfileDto> friends;

    //------- Game data --------
    private int fitwGamesPlayed;
    private int fitwGamesWon;
    private int fitwStepsMade;

    private int tictactoeGamesPlayed;
    private int tictactoeGamesWon;
    private int tictactoeMovesDone;

    public ProfileDto(User user) {
        this.username = user.getUsername();
        this.avatar = user.getAvatar();
        this.description = user.getDescription();
        this.friendss = new HashMap<>();
        this.friends = new ArrayList<>();
        for (User u : user.getUserFriends()) {
            friendss.put(u.getUsername(), u.getAvatar());
            friends.add(new ProfileDto(u.getUsername(), u.getAvatar()));
        }
        this.fitwGamesPlayed = user.getFitwStats().getGamesPlayed();
        this.fitwGamesWon = user.getFitwStats().getGamesWon();
        this.fitwStepsMade = user.getFitwStats().getStepsMade();
        this.tictactoeGamesPlayed = user.getTicTacToeStats().getGamesPlayed();
        this.tictactoeGamesWon = user.getTicTacToeStats().getGamesWon();
        this.tictactoeMovesDone = user.getTicTacToeStats().getMovesMade();
    }

    public ProfileDto(String username, String avatar) {
        this.username = username;
        this.avatar = avatar;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getFriendss() {
        return friendss;
    }

    public void setFriendss(Map<String, String> friendss) {
        this.friendss = friendss;
    }

    public List<ProfileDto> getFriends() {
        return friends;
    }

    public void setFriends(List<ProfileDto> friends) {
        this.friends = friends;
    }

    public int getFitwGamesPlayed() {
        return fitwGamesPlayed;
    }

    public void setFitwGamesPlayed(int fitwGamesPlayed) {
        this.fitwGamesPlayed = fitwGamesPlayed;
    }

    public int getFitwGamesWon() {
        return fitwGamesWon;
    }

    public void setFitwGamesWon(int fitwGamesWon) {
        this.fitwGamesWon = fitwGamesWon;
    }

    public int getFitwStepsMade() {
        return fitwStepsMade;
    }

    public void setFitwStepsMade(int fitwStepsMade) {
        this.fitwStepsMade = fitwStepsMade;
    }

    public int getTictactoeGamesPlayed() {
        return tictactoeGamesPlayed;
    }

    public void setTictactoeGamesPlayed(int tictactoeGamesPlayed) {
        this.tictactoeGamesPlayed = tictactoeGamesPlayed;
    }

    public int getTictactoeGamesWon() {
        return tictactoeGamesWon;
    }

    public void setTictactoeGamesWon(int tictactoeGamesWon) {
        this.tictactoeGamesWon = tictactoeGamesWon;
    }

    public int getTictactoeMovesDone() {
        return tictactoeMovesDone;
    }

    public void setTictactoeMovesDone(int tictactoeMovesDone) {
        this.tictactoeMovesDone = tictactoeMovesDone;
    }
}
