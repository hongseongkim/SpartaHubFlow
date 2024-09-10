package com.sparta.hotsix.user.service;
import com.sparta.hotsix.user.jwt.JwtUtil;
import com.sparta.hotsix.user.domain.User;
import com.sparta.hotsix.user.dto.SignInRequest;
import com.sparta.hotsix.user.dto.SignUpRequest;
import com.sparta.hotsix.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User signUp(SignUpRequest request) {


        if (findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }

        User user = new User(request);


        return userRepository.save(user);
    }


    public String signIn(SignInRequest request) {

       User user = findByEmail(request.getEmail())
               .orElseThrow(()-> new IllegalArgumentException("이메일을 찾을수 없습니다."));

       return jwtUtil.generateToken(user);

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(String userEmail) {
        
        return findByEmail(userEmail).orElseThrow(()-> new IllegalArgumentException("이메일을 찾을수 없습니다."));
    }

    public User getUserCheck(String userEmail) {

        return findByEmail(userEmail)
                .orElseThrow(()-> new IllegalArgumentException("이메일을 찾을수 없습니다."));
    }



}
