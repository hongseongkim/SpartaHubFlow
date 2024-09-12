package com.sparta.hotsix.user.service;

import com.sparta.hotsix.user.domain.User;
import com.sparta.hotsix.user.dto.SignInRequest;
import com.sparta.hotsix.user.dto.SignUpRequest;
import com.sparta.hotsix.user.dto.UserResponse;
import com.sparta.hotsix.user.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserResponse signUp (SignUpRequest request);
    String signIn(SignInRequest request);
    User findUserByEmail(String userEmail);
    UserResponse getUser(String userEmail);
    Page<UserResponse> getAllUsers(Pageable pageable);
    void updateUser(String userEmail, UserUpdateRequest userUpdateRequest);
    boolean softDelete(Long id);
    boolean softDelete(Long id,String userRole);

    Page<UserResponse> getUserSearch(Pageable modifiedPageable ,String username);
}
