package project.gamepage.data.model.game.stats;

import jakarta.persistence.*;
import project.gamepage.data.model.user.User;

@Entity
@Table(name = "stats_fitw")
public class FitwStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fitw_id")
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String username;
    private int gamesPlayed;
    private int gamesWon;
    private int stepsMade;

    public FitwStats() {
    }

    public FitwStats(User user, int gamesPlayed, int gamesWon, int stepsMade) {
        this.user = user;
        this.username = user.getUsername();
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.stepsMade = stepsMade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getStepsMade() {
        return stepsMade;
    }

    public void setStepsMade(int stepsMade) {
        this.stepsMade = stepsMade;
    }
}
