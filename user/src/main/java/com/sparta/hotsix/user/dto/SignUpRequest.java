package com.sparta.hotsix.user.dto;

import com.sparta.hotsix.user.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "사용자 이름은 필수 입력 항목입니다.")
    @Size(max = 100, message = "사용자 이름은 100자 이하여야 합니다.")
    private String username;

    @Size(max = 100, message = "닉네임은 100자 이하여야 합니다.")
    private String nickname;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 주소여야 합니다.")
    @Size(max = 255, message = "이메일은 255자 이하여야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;


    @NotBlank( message = "권한은 필수 입력 항목입니다.")
    private String role;


}
