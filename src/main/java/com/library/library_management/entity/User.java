package com.library.library_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            unique = true
    )
    private String username;

    @Column(nullable = false)
    private  String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name="role_id",
            nullable = false
    )
    private Role role;
}
