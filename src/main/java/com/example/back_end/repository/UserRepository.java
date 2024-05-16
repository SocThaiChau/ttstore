package com.example.back_end.repository;

import com.example.back_end.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    User getUserByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);
}
