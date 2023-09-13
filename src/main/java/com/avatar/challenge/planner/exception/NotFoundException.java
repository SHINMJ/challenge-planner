package com.avatar.challenge.planner.exception;

public class NotFoundException extends BizException{
    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }
}
