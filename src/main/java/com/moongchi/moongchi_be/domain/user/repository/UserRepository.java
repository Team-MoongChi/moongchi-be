package com.moongchi.moongchi_be.domain.user.repository;

import com.moongchi.moongchi_be.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndEmail(String provider, String email);

    @Query("SELECT u FROM User u WHERE u.id > 200")
    List<User> findAllByIdGreaterThanEqual200();
}
