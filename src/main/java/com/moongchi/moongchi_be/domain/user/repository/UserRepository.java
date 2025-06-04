package com.moongchi.moongchi_be.domain.user.repository;

import com.moongchi.moongchi_be.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndEmail(String provider, String email);
    Optional<User> findByEmail(String email);
}
