package com.avatar.challenge.planner.exception;

public class InvalidArgumentException extends BizException{
    public InvalidArgumentException() {
        super("입력값의 형식이 잘못되었습니다.");
    }

    public InvalidArgumentException(String message) {
        super(message);
    }
}
