package com.avatar.challenge.planner.exception;

public class UnauthorizedException extends BizException{
    public UnauthorizedException() {
        super("권한이 없습니다.");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
