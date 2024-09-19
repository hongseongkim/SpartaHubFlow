package com.sparta.hotsix.user.contoller;

import com.sparta.hotsix.user.contoller.util.PageableUtils;
import com.sparta.hotsix.user.domain.UserRole;
import com.sparta.hotsix.user.dto.*;
import com.sparta.hotsix.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        return ResponseEntity.ok(new SignInResponse(userService.signIn(signInRequest)));
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {

        UserResponse userResponse = userService.signUp(signUpRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestHeader(value = "User-Email") String userEmail,
            @RequestHeader(value = "User-Role") String userRole,
            @PageableDefault() Pageable pageable
    ) {
        Pageable modifiedPageable = PageableUtils.applyPageSizeLimit(pageable);
        modifiedPageable = PageableUtils.applyDefaultSortIfNecessary(modifiedPageable);

        if (!Objects.equals(userRole, "MASTER")) {
            return ResponseEntity.ok().body(userService.getUser(userEmail));
        }
        Page<UserResponse> usersPage = userService.getAllUsers(modifiedPageable);
        return ResponseEntity.ok().body(usersPage);
    }


    @GetMapping("/search")
    public ResponseEntity<?> getUserSearch(
            @PageableDefault() Pageable pageable
            , @RequestParam String username
    ) {
        Pageable modifiedPageable = PageableUtils.applyPageSizeLimit(pageable);
        modifiedPageable = PageableUtils.applyDefaultSortIfNecessary(modifiedPageable);
        Page<UserResponse> usersPage = userService.getUserSearch(modifiedPageable, username);
        return ResponseEntity.ok().body(usersPage);
    }


    // admin 권한으로 특정 user 정보 불러오기
    @GetMapping("/admin")
    public ResponseEntity<?> adminGetUser(@RequestParam String userEmail) {
        return ResponseEntity.ok().body(userService.getUser(userEmail));
    }


    @PatchMapping("/update")
    public ResponseEntity<?> updateUser(@RequestHeader(value = "User-Email") String userEmail,
                                        @Valid @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        if (userEmail.equals(userUpdateRequest.getEmail())) {
            UserResponse userResponse = userService.updateUser(userEmail, userUpdateRequest);
            return ResponseEntity.ok().body(userResponse);
        }
        return ResponseEntity.badRequest().body("이메일이 일치하지 않습니다.");
    }


    // 일반 유저 및 관리자 삭제 처리
    @DeleteMapping("/delete")
    public ResponseEntity<?> softDelete(@RequestHeader(value = "User-Id") Long userId) {
        UserResponse isDeleted = userService.softDelete(userId);
            return ResponseEntity.ok().body(isDeleted);

    }


    // admin 이 Param 보낸 id 삭제
    @DeleteMapping("/admin")
    public ResponseEntity<?> adminDelete(
            @RequestHeader(value = "User-Id") Long adminId,
            @RequestParam Long userId) {
        UserResponse isDeleted = userService.softDelete(userId,adminId);

        return ResponseEntity.badRequest().body(isDeleted);
    }



}








