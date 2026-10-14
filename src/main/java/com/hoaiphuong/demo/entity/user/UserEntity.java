package com.hoaiphuong.demo.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
public class UserEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "VARCHAR(255)",name = "name",nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Builder
    public UserEntity(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void update(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
