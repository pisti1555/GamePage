package project.gamepage.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.gamepage.data.model.game.stats.TicTacToeStats;

@Repository
public interface TicTacToeRepository extends JpaRepository<TicTacToeStats, Long> {
    TicTacToeStats findByUsername(String username);
}
