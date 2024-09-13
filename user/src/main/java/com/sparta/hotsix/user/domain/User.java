package com.sparta.hotsix.user.domain;

import com.sparta.hotsix.user.dto.SignUpRequest;
import com.sparta.hotsix.user.dto.UserUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "email", length = 255, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role", length = 20)
    private UserRole role;

    @Column(name = "is_deleted")
    private boolean isDeleted;




    public User(SignUpRequest request,String encodedPassword) {
        this.username = request.getUsername();
        this.nickname = request.getNickname();
        this.email = request.getEmail();
        this.password = encodedPassword;
        this.role = UserRole.valueOf(request.getRole());
    }

    public void update(UserUpdateRequest request , String encodedPassword) {
       this.nickname = request.getNickname();
       this.email = request.getEmail();
       this.username = request.getUsername();
        this.password = encodedPassword;

    }




    public void softDelete(String adminUsername) {
        this.isDeleted = true;
            setDelete(LocalDateTime.now(), "ADMIN : " + adminUsername);
    }


    public void softDelete() {
        this.isDeleted = true;
        setDelete(LocalDateTime.now(), username);
    }

}
