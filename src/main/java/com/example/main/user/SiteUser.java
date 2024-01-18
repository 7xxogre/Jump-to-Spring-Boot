package com.example.main.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(unique=true)
    private String username;

    private String password;

    @Column(unique=true)
    private String email;

    private String registerId;
    @Builder
    public SiteUser() {
    }
    @Builder
    public SiteUser(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
