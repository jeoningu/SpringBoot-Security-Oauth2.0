package com.jig.security1.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;
    private String role; //  ROLE_USER, ROLE_ADMIN
    private String provider; // 어떤 OAuth2 인증인지 ex)google
    private String providerId; // OAuth2 인증에서 사용되는 각 유저의 primary key ex)google에서는 sub값
    @CreationTimestamp
    private Timestamp createDate;

    // id, createDate는 db에서 자동 추가되는 필드여서 builder에서 입력 받지 않음
    @Builder
    public User(String username, String password, String email, String role, String provider, String providerId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }
}
