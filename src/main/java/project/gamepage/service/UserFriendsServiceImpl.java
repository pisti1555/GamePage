package project.gamepage.service;

import project.gamepage.data.model.user.User;
import project.gamepage.data.repository.FriendRequestDao;
import project.gamepage.data.repository.UserFriendDao;
import project.gamepage.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserFriendsServiceImpl implements UserFriendsService {

    private final FriendRequestDao request;
    private final UserFriendDao dao;
    private final UserRepository userRepository;

    @Autowired
    public UserFriendsServiceImpl(UserFriendDao dao, FriendRequestDao request, UserRepository userRepository) {
        this.request = request;
        this.dao = dao;
        this.userRepository = userRepository;
    }

    private User saveIfNotExist(String username) {
        User existingUser = this.request.findByUsername(username);
        if (existingUser == null) {
            existingUser = new User();
            existingUser.setUsername(username);
            return this.request.save(existingUser);
        } else {
            return existingUser;
        }
    }

    @Override
    public boolean addUserFriends(String un1, String un2) {
        if (un1.equals(un2)) {
            return false;
        }

        User user1 = userRepository.findByUsername(un1);
        User user2 = userRepository.findByUsername(un2);


        if (user1 == null || user2 == null) {
            return false;
        }

        user1 = this.saveIfNotExist(un1);
        user2 = this.saveIfNotExist(un2);

        if (user1.getUserFriendRequest().contains(user2)) {
            return false;
        }

        user1.sendFriendRequest(user2);
        this.request.save(user1);

        if (user1.getUserFriendRequest().contains(user2) && user2.getUserFriendRequest().contains(user1)) {
            user1.addFriend(user2);
            user2.addFriend(user1);

            this.dao.save(user1);
            this.dao.save(user2);

            this.request.deleteFriendRequest(user1.getId(), user2.getId());
            this.request.deleteFriendRequest(user2.getId(), user1.getId());
        }

        return true;
    }

    @Override
    public List<String> getUserFriendsList(String username) {
        User user = this.dao.findByUsername(username);
        if (user == null) return null;
        return user.getUserFriend().stream().map(User::getUsername).collect(Collectors.toList());
    }

    @Override
    public List<String> getFriendRequests(String username) {
        List<String> invites = new ArrayList<>();

        User client = userRepository.findByUsername(username);
        List<User> requests = userRepository.findAll();

        for (User user : requests) {
            if (user.getUserFriendRequest().contains(client)) {
                if (!isFriend(username, user.getUsername())) {
                    invites.add(user.getUsername());
                }
            }
        }

        return invites;
    }

    @Override
    public int getFriendRequestCount(String principal) {
        return getFriendRequests(principal).size();
    }

    @Override
    public void declineFriendRequest(String inviter, String invited) {
        User inviterUser = request.findByUsername(inviter);
        User invitedUser = request.findByUsername(invited);
        if (inviterUser != null && invitedUser != null) {
            Long inviterId = inviterUser.getId();
            Long invitedId = invitedUser.getId();

            request.deleteFriendRequest(inviterId, invitedId);
            request.deleteFriendRequest(invitedId, inviterId);
        }
    }

    @Override
    public void deleteFriends(String username1, String username2) {
        User user1 = request.findByUsername(username1);
        User user2 = request.findByUsername(username2);
        if (user1 != null && user2 != null) {
            Long user1Id = user1.getId();
            Long user2Id = user2.getId();

            dao.deleteFriend(user1Id, user2Id);
            dao.deleteFriend(user2Id,  user1Id);
        }
    }


    @Override
    public boolean isFriend(String principal, String username) {
        User client = this.dao.findByUsername(principal);
        User user = this.dao.findByUsername(username);

        return user != null && user.getUserFriend().contains(client) || client == user;
    }

    @Override
    public boolean isFriendInvitationSent(String principal, String username) {
        User client = this.dao.findByUsername(principal);
        User user = this.dao.findByUsername(username);

        return user != null &&
                client.getUserFriendRequest().contains(user);
    }
}
