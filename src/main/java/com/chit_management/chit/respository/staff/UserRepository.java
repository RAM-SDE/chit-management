package com.chit_management.chit.respository.staff;

import com.chit_management.chit.entity.staff.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<User> findByUuid(String Uuid);
}
