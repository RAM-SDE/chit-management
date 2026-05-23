package com.chit_management.chit.entity.staff;

import com.chit_management.chit.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;          // e.g. ADMIN, STUDENT

    private String description;

    @Column(name = "is_active",columnDefinition = "TINYINT(1)")
    private Boolean isActive = true;
}

