package com.angular_training.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column()
    private String username;
    @Column()
    private String password;
    @Column()
    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        ROLE_ADMIN,
        ROLE_USER
    }
}
