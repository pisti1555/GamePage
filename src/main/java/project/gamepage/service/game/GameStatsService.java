package project.gamepage.service.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.gamepage.data.model.game.stats.FitwStats;
import project.gamepage.data.model.game.stats.TicTacToeStats;
import project.gamepage.data.model.user.User;
import project.gamepage.data.repository.FitwRepository;
import project.gamepage.data.repository.TicTacToeRepository;
import project.gamepage.data.repository.UserRepository;

@Service
public class GameStatsService {
    private final UserRepository userRepository;
    private final FitwRepository fitwRepository;
    private final TicTacToeRepository ticTacToeRepository;

    @Autowired
    public GameStatsService(UserRepository userRepository, FitwRepository fitwRepository, TicTacToeRepository ticTacToeRepository) {
        this.userRepository = userRepository;
        this.fitwRepository = fitwRepository;
        this.ticTacToeRepository = ticTacToeRepository;
    }

    public FitwStats findByUsername_Fitw(String username) {
        return fitwRepository.findByUsername(username);
    }
    public TicTacToeStats findByUsername_TicTacToe(String username) {
        return ticTacToeRepository.findByUsername(username);
    }

    @Transactional
    public FitwStats saveFitw(FitwStats fitwStats) {
        return fitwRepository.save(fitwStats);
    }
    @Transactional
    public TicTacToeStats saveTicTacToe(TicTacToeStats ticTacToeStats) {
        return ticTacToeRepository.save(ticTacToeStats);
    }
}
