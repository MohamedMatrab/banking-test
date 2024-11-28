package com.exam_jee.ds.repositories;

import com.exam_jee.ds.model.Role;
import com.exam_jee.ds.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String userId);
    Boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    Boolean existsByEmail(String email);

    List<User> findByRole(Role role);

}
