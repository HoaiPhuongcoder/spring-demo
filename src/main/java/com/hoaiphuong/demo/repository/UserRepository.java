package com.hoaiphuong.demo.repository;

import com.hoaiphuong.demo.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    UserEntity findByNameAndEmail(String name, String email);

    UserEntity findByEmail(String email);

    Page<UserEntity> findByNameContainsIgnoreCase(String name, Pageable pageable);
}
