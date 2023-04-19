package com.synchrony.assignment.userprofile.repository;

import com.synchrony.assignment.userprofile.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring DATA JPA repository for User
 *
 * @author sanketku
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}
