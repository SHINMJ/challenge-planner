package com.avatar.challenge.planner.user.domain;

import com.avatar.challenge.planner.common.BaseEntity;
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

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User extends BaseEntity {
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

    private void validate(String email, String password){
        if (!StringUtils.hasLength(email)){
            throw new RequiredArgumentException("이메일은 필수입니다.");
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
