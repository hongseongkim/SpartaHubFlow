package com.sparta.hotsix.user.contoller;

import com.sparta.hotsix.user.domain.UserRole;
import com.sparta.hotsix.user.dto.SignInRequest;
import com.sparta.hotsix.user.dto.SignInResponse;
import com.sparta.hotsix.user.dto.SignUpRequest;
import com.sparta.hotsix.user.service.AuditingConfig;
import com.sparta.hotsix.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody SignInRequest signInRequest) {

        return ResponseEntity.ok(new SignInResponse(userService.signIn(signInRequest)));
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) {
        AuditingConfig.setCurrentAuditor(signUpRequest.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signUp(signUpRequest));
    }


    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestHeader(value = "User-Email", required = false) String userEmail,
            @RequestHeader(value = "User-Id", required = false) String userId,
            @RequestHeader(value = "User-Role",required = false) String userRole
    ) {

        System.out.println("userEmail + userId+ userRole = " + userEmail + userId+ userRole);

        if (userService.getUserCheck(userEmail).getRole() != UserRole.MASTER) {
           return ResponseEntity.ok().body(userService.getUser(userEmail));
        }

        return ResponseEntity.ok().body(userService.getAllUsers());

    }


    @GetMapping("/admin")
    public ResponseEntity<?> getUser(@RequestParam String userEmail) {

        return ResponseEntity.ok().body(userService.getUser(userEmail));

    }


}
