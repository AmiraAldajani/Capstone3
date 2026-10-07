package com.example.ejadwebapplication.Repository;

import com.example.ejadwebapplication.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserById(Integer id);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<User> findAllByFullNameContainingIgnoreCase(String name);
}