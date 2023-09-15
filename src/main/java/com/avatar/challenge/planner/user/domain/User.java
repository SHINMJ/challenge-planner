package com.avatar.challenge.planner.user.domain;

import com.avatar.challenge.planner.common.BaseEntity;
import com.avatar.challenge.planner.exception.InvalidArgumentException;
import com.avatar.challenge.planner.exception.RequiredArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User extends BaseEntity {
    private static final String EMAIL_REGEX = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Id
    private Long id;

    private String email;

    private String password;
    private String nickname;
    private String refreshToken;
    @Column("activated_at")
    private Boolean activate = Boolean.TRUE;

    private User(String email, String password, String nickname){
        validate(email, password);
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public static User of(String email, String password, String nickname){
        return new User(email, password, nickname);
    }

    public User updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public User updatePassword(String encodedPassword) {
        this.password = encodedPassword;
        return this;
    }

    private void validate(String email, String password){

        if (!StringUtils.hasLength(email)){
            throw new RequiredArgumentException("이메일은 필수입니다.");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()){
            throw new InvalidArgumentException("이메일 형식이 잘못되었습니다.");
        }

        if (!StringUtils.hasLength(password)) {
            throw new RequiredArgumentException("비밀번호는 필수입니다.");
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
