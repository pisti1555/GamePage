package project.gamepage.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.gamepage.data.model.game.stats.FitwStats;

@Repository
public interface FitwRepository extends JpaRepository<FitwStats, Long> {
    FitwStats findByUsername(String username);

}
