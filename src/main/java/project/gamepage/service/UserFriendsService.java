package project.gamepage.service;

import project.gamepage.web.dto.ProfileDto;

import java.util.List;

public interface UserFriendsService {
    boolean addUserFriends(String un1, String un2);
    List<ProfileDto> getUserFriendsList(String username);
    List<ProfileDto> getUnaddedUsers(String username);
    List<ProfileDto> getFriendRequests(String principal);
    int getFriendRequestCount(String principal);
    void declineFriendRequest(String inviter, String invited);
    void deleteFriends(String user1Id, String user2Id);
    boolean isFriend(String principal, String username);
    boolean isFriendInvitationSent(String principal, String username);
}
