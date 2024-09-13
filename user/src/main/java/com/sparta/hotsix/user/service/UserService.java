package com.sparta.hotsix.user.service;

import com.sparta.hotsix.user.domain.User;
import com.sparta.hotsix.user.dto.SignInRequest;
import com.sparta.hotsix.user.dto.SignUpRequest;
import com.sparta.hotsix.user.dto.UserResponse;
import com.sparta.hotsix.user.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {

    UserResponse signUp (SignUpRequest signUpRequest);
    String signIn(SignInRequest signInRequest);
    User findUserByEmail(String userEmail);
    UserResponse getUser(String userEmail);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse updateUser(String userEmail, UserUpdateRequest userUpdateRequest);
    UserResponse softDelete(Long id);
    UserResponse softDelete(Long id,Long adminId);

    Page<UserResponse> getUserSearch(Pageable modifiedPageable ,String username);
}
