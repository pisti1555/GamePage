package project.gamepage.data.repository;

import project.gamepage.data.model.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findByUsername(String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserFriends friends WHERE friends.user.id = :user1 AND friends.friendId = :user2")
    void deleteFriend(Long user1, Long user2);

}
