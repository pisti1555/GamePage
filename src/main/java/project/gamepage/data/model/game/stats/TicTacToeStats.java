package project.gamepage.data.model.game.stats;

import jakarta.persistence.*;
import project.gamepage.data.model.user.User;

@Entity
@Table(name = "stats_tic_tac_toe")
public class TicTacToeStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tictactoe_id")
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    private int gamesPlayed;
    private int gamesWon;
    private int movesMade;

    public TicTacToeStats(User user, int gamesPlayed, int gamesWon, int movesMade) {
        this.user = user;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.movesMade = movesMade;
    }

    public TicTacToeStats() {
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

    public int getMovesMade() {
        return movesMade;
    }

    public void setMovesMade(int movesMade) {
        this.movesMade = movesMade;
    }
}