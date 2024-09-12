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
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signUp(signUpRequest));
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestHeader(value = "User-Email", required = false) String userEmail,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Pageable modifiedPageable = PageableUtils.applyPageSizeLimit(pageable);
        modifiedPageable = PageableUtils.applyDefaultSortIfNecessary(modifiedPageable);

        if (userEmail != null && userService.findUserByEmail(userEmail).getRole() != UserRole.MASTER) {
            return ResponseEntity.ok().body(userService.getUser(userEmail));
        }

        Page<UserResponse> usersPage = userService.getAllUsers(modifiedPageable);
        return ResponseEntity.ok().body(usersPage);
    }


    @GetMapping("/search")
    public ResponseEntity<?> getUserSearch(
            @PageableDefault(size = 10) Pageable pageable
            ,@RequestParam String username
    ) {
        Pageable modifiedPageable = PageableUtils.applyPageSizeLimit(pageable);
        modifiedPageable = PageableUtils.applyDefaultSortIfNecessary(modifiedPageable);


        Page<UserResponse> usersPage = userService.getUserSearch(modifiedPageable,username);
        return ResponseEntity.ok().body(usersPage);
    }


    // admin 권한으로 특정 user 정보 불러오기
    @GetMapping("/admin")
    public ResponseEntity<?> adminGetUser(@RequestParam String userEmail) {
        return ResponseEntity.ok().body(userService.getUser(userEmail));
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateUser(@RequestHeader(value = "User-Email", required = false) String userEmail,
                                        @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        if (userEmail.equals(userUpdateRequest.getEmail())) {
            userService.updateUser(userEmail, userUpdateRequest);
            return ResponseEntity.ok().body(userService.getUser(userEmail));
        }
        return ResponseEntity.badRequest().body("이메일이 일치하지 않습니다.");
    }

    // 일반 유저 및 관리자 삭제 처리
    @DeleteMapping("/delete")
    public ResponseEntity<?> softDelete(@RequestHeader(value = "User-Id", required = false) Long userId) {
        boolean isDeleted = userService.softDelete(userId);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().body("사용자 삭제 실패");


    }


    // admin 이 Param 보낸 id 삭제
    @DeleteMapping("/admin")
    public ResponseEntity<?> adminDelete(
            @RequestHeader(value = "User-Role", required = false) String userRole,
            @RequestParam Long userId) {

        boolean isDeleted = userService.softDelete(userId,userRole);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().body("사용자 삭제 실패");

    }


}








