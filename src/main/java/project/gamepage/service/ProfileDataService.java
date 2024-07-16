package project.gamepage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import project.gamepage.data.model.user.User;
import project.gamepage.web.dto.ProfileDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileDataService {
    private final UserService userService;
    private final UserFriendsService friendsService;
    @Autowired
    public ProfileDataService(UserService userService, UserFriendsService friendsService) {
        this.userService = userService;
        this.friendsService = friendsService;
    }

    public List<ProfileDto> searchUsers(@RequestParam("query")String query) {
        List<ProfileDto> users = new ArrayList<>();
        for (User u : userService.searchUsers(query)) {
            users.add(new ProfileDto(u.getUsername(), u.getAvatar()));
        }
        return users;
    }

    public ProfileDto getProfileData(String username) {
        return new ProfileDto(userService.findByUsername(username));
    }

    public List<ProfileDto> getAll() {
        List<ProfileDto> list = new ArrayList<>();
        for (User u : userService.findAll()) {
            list.add(new ProfileDto(u));
        }
        return list;
    }



    // ---------------------- Friends --------------------------
    public boolean addFriend(String inviter, String invited) {
        User invitedUser = userService.findByUsername(invited);
        if (invitedUser == null) return false;
        friendsService.addUserFriends(inviter, invited);
        return true;
    }

    public List<ProfileDto> showUnaddedFriends(String username) {
        List<ProfileDto> users = new ArrayList<>();
        for (User i : userService.findAll()) {
            boolean areFriends = false;
            for (ProfileDto j : friendsService.getUserFriendsList(username)) {
                if (i.getUsername().equals(j.getUsername()) || i.getUsername().equals(username)) {
                    areFriends = true;
                    break;
                }
            }
            if (!areFriends) users.add(new ProfileDto(i.getUsername(), i.getAvatar()));
        }
        return users;
    }





}
