package com.hoaiphuong.demo.repository;

import com.hoaiphuong.demo.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// Email lookups ignore case so rows saved before email normalization are still matched
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByNameAndEmailIgnoreCase(String name, String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);

    Page<UserEntity> findByNameContainsIgnoreCase(String name, Pageable pageable);
}
