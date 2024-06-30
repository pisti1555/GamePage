package project.gamepage.service;

import java.util.List;

public interface UserFriendsService {
    boolean addUserFriends(String un1, String un2);
    List<String> getUserFriendsList(String username);
    List<String> getFriendRequests(String principal);
    int getFriendRequestCount(String principal);
    void declineFriendRequest(String inviter, String invited);
    void deleteFriends(String user1Id, String user2Id);
    boolean isFriend(String principal, String username);
    boolean isFriendInvitationSent(String principal, String username);
}
