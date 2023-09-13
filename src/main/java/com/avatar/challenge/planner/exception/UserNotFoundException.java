package com.avatar.challenge.planner.exception;

public class UserNotFoundException extends NotFoundException{
    public UserNotFoundException() {
        super("사용자를 찾을 수 없습니다.");
    }

}
