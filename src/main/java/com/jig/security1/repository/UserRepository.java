package com.jig.security1.repository;

import com.jig.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    // SELECT * user FROM WHERE username = 1?
    User findByUsername(String username); // JPA Query Methods 키워드로 검색해서 공부

}
