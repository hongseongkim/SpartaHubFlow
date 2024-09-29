package com.sparta.hotsix.user.service;

import com.sparta.hotsix.user.domain.UserRole;
import com.sparta.hotsix.user.dto.UserResponse;
import com.sparta.hotsix.user.dto.UserUpdateRequest;
import com.sparta.hotsix.user.jwt.JwtUtil;
import com.sparta.hotsix.user.domain.User;
import com.sparta.hotsix.user.dto.SignInRequest;
import com.sparta.hotsix.user.dto.SignUpRequest;
import com.sparta.hotsix.user.jwt.TokenService;
import com.sparta.hotsix.user.repository.UserQueryRepository;
import com.sparta.hotsix.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserQueryRepository userQueryRepository;


    // 유저 ID로 유저 찾기 (중복된 예외 처리 메서드로 분리)
    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("아이디를 찾을 수 없습니다."));
    }

    // 이메일로 유저 찾기
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 이메일로 유저 찾기 (중복된 예외 처리 메서드로 분리)


    public User findUserByEmail(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일을 찾을 수 없습니다."));
    }
    // 회원가입 처리
    @Transactional
    public UserResponse signUp(SignUpRequest signUpRequest ) {


        if (findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User(signUpRequest, passwordEncoder.encode(signUpRequest.getPassword()));
        userRepository.save(user);

        return convertToUserResponse(user);
    }

    // 로그인 처리
    public String signIn(SignInRequest request) {
        User user = findUserByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀립니다.");
        }

        return tokenService.getOrGenerateToken(user);
    }

    @Override
    public Page<UserResponse> getUserSearch(Pageable pageable, String username) {
        Page<User> usersPage = userQueryRepository.getUserSearch(pageable, username);
        return usersPage.map(this::convertToUserResponse);
    }

    // 모든 유저 정보 가져오기
    @Override
    @Cacheable(cacheNames = "userAllCache", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userQueryRepository.findAll(pageable);
        return usersPage.map(this::convertToUserResponse);
    }

    // 특정 유저 정보 가져오기
    @Cacheable(cacheNames = "userCache", key = "args[0]")
    public UserResponse getUser(String userEmail) {
        User user = findUserByEmail(userEmail);
        return convertToUserResponse(user);
    }

    // 유저 정보 업데이트
    @Override
    @Transactional
    @CachePut(cacheNames = "userCache" ,  key = "args[0]")
    @CacheEvict(cacheNames = "userAllCache", allEntries = true)
    public UserResponse updateUser(String userEmail, UserUpdateRequest request) {
        User user = findUserByEmail(userEmail);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 틀립니다.");
        }

        user.update(request, passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return convertToUserResponse(user);

    }



    // 유저 소프트 삭제
    @Override
    @Transactional
    @CacheEvict(cacheNames = {"userCache", "userAllCache"}, allEntries = true)
    public UserResponse softDelete(Long id) {
        User user = findUserById(id);
        user.softDelete();
        userRepository.save(user);
        return convertToUserResponse(user);
    }

    // 관리자 권한으로 유저 소프트 삭제
    @Override
    @Transactional
    @CachePut(cacheNames = "userCache", key = "#result.email")
    @CacheEvict(cacheNames = "userAllCache", allEntries = true)
    public UserResponse softDelete(Long id, Long adminId) {
        User user = findUserById(id);
        User admin = findUserById(adminId);

        if (Objects.equals(admin.getRole(), "MASTER")) {
            user.softDelete(admin.getUsername());
            userRepository.save(user);
            return convertToUserResponse(user);
        } else {
            throw new IllegalArgumentException("MASTER 권한 없음");
        }
    }


    // 엔티티 -> DTO 변환 메서드 (private 접근 제한자 사용)
    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(

                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getRole().name(),
                user.isDeleted(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt(),
                user.getDeletedBy()
        );
    }

}