package com.sparta.hotsix.user.domain;

import com.sparta.hotsix.user.dto.SignUpRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "email", length = 255, unique = true)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private UserRole role;

    @Column(name = "is_public")
    private Boolean isPublic = true;




    public User(SignUpRequest request) {
        this.username = request.getUsername();
        this.nickname = request.getNickname();
        this.email = request.getEmail();
        this.password = request.getPassword();
        this.role = UserRole.CUSTOMER;
    }
}
