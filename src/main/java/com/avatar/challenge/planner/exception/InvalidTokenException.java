package com.avatar.challenge.planner.exception;

public class InvalidTokenException extends BizException{
    public InvalidTokenException() {
        super("토큰이 잘못되었습니다.");
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
