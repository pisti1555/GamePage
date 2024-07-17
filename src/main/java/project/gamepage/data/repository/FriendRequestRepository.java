package project.gamepage.data.repository;

import project.gamepage.data.model.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findByUsername(String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserFriendRequest requests WHERE requests.user.id = :user1 AND requests.friendId = :user2")
    void deleteFriendRequest(Long user1, Long user2);

}