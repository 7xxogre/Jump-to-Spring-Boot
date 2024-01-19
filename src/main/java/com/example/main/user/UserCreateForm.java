package com.example.main.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;


@Getter
@Setter
public class UserCreateForm {
    @Size(min = 3, max = 25, message = "아이디 길이는 3~25 사이여야 합니다.")
    @NotEmpty(message = "사용자ID는 필수항목입니다.")
    @Pattern(regexp = "^[^@]+$", message = "아이디에 '@' 문자를 포함할 수 없습니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String password1;

    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String password2;

    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email
    private String email;
}