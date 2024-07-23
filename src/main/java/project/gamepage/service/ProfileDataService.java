package project.gamepage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import project.gamepage.data.model.user.User;
import project.gamepage.web.dto.ProfileDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
}