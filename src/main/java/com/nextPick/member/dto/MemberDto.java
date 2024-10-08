package com.nextPick.member.dto;

import com.nextPick.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class MemberDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Post {
        @NotNull(message = "이메일은 필수 항목입니다.")
        @Email
        private String email;

        @NotNull
        private String name;

        @NotNull(message = "성별은 필수 항목입니다.")
        @Pattern(regexp = "^[MF]$")
        private String gender;

        @NotNull(message = "직군선택은 필수 항목입니다.")
        private Member.memberOccupation occupation;

        @NotNull(message = "비밀번호는 필수 항목입니다.")
        @Size(min = 8, max = 15, message = "비밀번호는 8자에서 15자 사이여야 합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~]{8,15}$",
                message = "비밀번호는 8자이상 15자 이하의 알파벳, 숫자, 특수문자만 포함할 수 있습니다.")
        private String password;

        @NotNull(message = "비밀번호를 한번더 입력해주세요.")
        private String confirmPassword;

        @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,8}$",
                message = "특수문자 제외 2자이상 8자 이하로 입력해주세요.")
        private String nickname;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Patch {
        @Size(min = 8, max = 20, message = "비밀번호는 8자에서 20자 사이여야 합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~]+$",
                message = "비밀번호는 알파벳, 숫자, 특수문자만 포함할 수 있습니다.")
        private String password;

        private String confirmPassword;

        @Pattern(regexp = "^[a-zA-Z가-힣]+$",
                message = "숫자와 특수문자는 사용할 수 없습니다. 알파벳과 한글만 입력해 주세요.")
        private String nickname;

        private Member.memberOccupation occupation;

        private Integer guiltyScore;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    public static class AdminPatch {
        private Member.memberType memberType;
        private Integer guiltyScore;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DuplicationEmailCheck{
        @NotNull
        @Email
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DuplicationNicknameCheck{
        @NotNull
        @Email
        private String nickName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response{
        private String name;
        private String gender;
        private String email;
        private String nickname;
        private Member.memberOccupation occupation;
        private Member.memberStatus status;
        private Member.memberType type;
        private List<String> roles;
    }
}
