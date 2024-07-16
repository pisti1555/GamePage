package project.gamepage.web.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import project.gamepage.service.ProfileDataService;
import project.gamepage.service.UserFriendsService;
import project.gamepage.service.UserService;
import project.gamepage.web.dto.ProfileDto;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
public class ProfileDtoController {
    private final ProfileDataService service;
    private final SimpMessagingTemplate template;
    @Autowired
    public ProfileDtoController(ProfileDataService service, UserService userService, UserFriendsService friendsService, SimpMessagingTemplate template) {
        this.service = service;
        this.template = template;
    }

    @GetMapping("/search")
    private List<ProfileDto> searchUsers(@RequestParam("query")String query) {
        return service.searchUsers(query);
    }

    @GetMapping("/get-all")
    private List<ProfileDto> getAll() {
        return service.getAll();
    }


    // ---------------- Friends --------------------
    @GetMapping("/friends/show-unadded-players")
    private List<ProfileDto> showUnaddedFriends(Principal principal) {
        return service.showUnaddedFriends(principal.getName());
    }
}
