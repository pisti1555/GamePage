package project.gamepage.data.model.user;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;
import project.gamepage.data.model.game.stats.FitwStats;
import project.gamepage.data.model.game.stats.TicTacToeStats;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "id"),
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String avatar;
    private String description;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    @ManyToMany
    @JoinTable(name = "user_friend_requests", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "friend_id") )
    private Set<User> userFriendRequests;
    @ManyToMany
    @JoinTable(name = "user_friends", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "friend_id") )
    private Set<User> userFriends;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JoinTable(name = "stats_fitw", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "fitw_id") )
    private FitwStats fitwStats;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JoinTable(name = "stats_tictactoe", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "tictactoe_id") )
    private TicTacToeStats ticTacToeStats;

    public User() {
        super();
        this.roles = new HashSet<>();
    }
    public User(String username, String avatar, String email, String password, String firstName, String lastName, Collection<Role> roles) {
        this.username = username;
        this.avatar = avatar;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
        this.ticTacToeStats = new TicTacToeStats(this, 0, 0, 0);
        this.fitwStats = new FitwStats(this, 0, 0, 0);
    }

    public void sendFriendRequest(User user) {
        if (CollectionUtils.isEmpty(this.userFriendRequests)) {
            this.userFriendRequests = new HashSet<>();
        }
        this.userFriendRequests.add(user);
    }

    public void addFriend(User user) {
        if (CollectionUtils.isEmpty(this.userFriends)) {
            this.userFriends = new HashSet<>();
        }
        this.userFriends.add(user);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public Set<User> getUserFriendRequests() {
        return userFriendRequests;
    }

    public Set<User> getUserFriends() {
        return userFriends;
    }

    public void setUserFriends(Set<User> userFriends) {
        this.userFriends = userFriends;
    }

    public void setUserFriendRequests(Set<User> userFriendRequests) {
        this.userFriendRequests = userFriendRequests;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FitwStats getFitwStats() {
        return fitwStats;
    }

    public void setFitwStats(FitwStats fitwStats) {
        this.fitwStats = fitwStats;
    }

    public TicTacToeStats getTicTacToeStats() {
        return ticTacToeStats;
    }

    public void setTicTacToeStats(TicTacToeStats ticTacToeStats) {
        this.ticTacToeStats = ticTacToeStats;
    }
}