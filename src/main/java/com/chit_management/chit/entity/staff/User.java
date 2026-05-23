package com.chit_management.chit.entity.staff;

import com.chit_management.chit.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    private String password;

    @Column(length = 15)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns =
            @JoinColumn(name = "user_id"),
            inverseJoinColumns =
            @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}