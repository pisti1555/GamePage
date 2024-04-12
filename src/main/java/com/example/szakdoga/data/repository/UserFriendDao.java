package com.example.szakdoga.data.repository;

import com.example.szakdoga.data.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserFriendDao extends CrudRepository<User, Long> {
    User findByEmail(String email);
    User findByUsername(String username);
    //List<User> findAllByEmail(Set<String> emails);
}
