package com.tbp.tbp_auth.datamodel.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "app_user") // because User is a keyword in most databases
public class User {
    @Id
    @Getter @Setter
    private UUID id;

    @Getter @Setter
    @Column(nullable = false, unique = true)
    private String username;

    @Getter @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @Getter @Setter
    @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    private String steamId;

    @Getter @Setter
    @Column(nullable = false)
    private String passwordHash;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider = AuthProvider.LOCAL;

    @Getter @Setter
    private String providerId;

    @Getter @Setter
    private String role;

    public User()
    {
        this.id = UUID.randomUUID();
    }

    public User(UUID uuid, String email, String username) {
        this.id = uuid;
        this.email = email;
        this.username = username;
    }
}

