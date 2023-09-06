package com.avatar.challenge.planner.exception;

public class NotPositiveNumberException extends BizException{
    public NotPositiveNumberException() {
        super("양의 정수만 입력해 주세요.");
    }
}
