package com.example.szakdoga.data.repository;

import com.example.szakdoga.data.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestDao extends CrudRepository<User, Long> {
    User findByEmail(String email);
    User findByUsername(String username);
}
