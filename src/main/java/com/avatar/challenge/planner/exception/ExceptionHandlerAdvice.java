package com.avatar.challenge.planner.exception;

import com.avatar.challenge.planner.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(HttpClientErrorException.UnprocessableEntity.class)
    protected ResponseEntity<ErrorResponse> handleUnprocessableEntityException(HttpClientErrorException.UnprocessableEntity e){
        log.error("handleUnprocessableEntityException", e);
        return errorResponse(ErrorResponse.from("Unprocessable Entity"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException", e);
        return errorResponse(ErrorResponse.from(e.getName()));
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.error("handleNotFoundException", e);
        return errorResponse(ErrorResponse.of("Not Found", HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("handleAccessDeniedException", e);
        return errorResponse(ErrorResponse.of("Access Denied", HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    protected ResponseEntity<ErrorResponse> handleUnauthorizedException(HttpClientErrorException.Unauthorized e) {
        log.error("handleUnauthorizedException", e);
        return errorResponse(ErrorResponse.of("Unauthorized", HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(InvalidTokenException.class)
    protected ResponseEntity<ErrorResponse> handleTokenValidationException(InvalidTokenException e) {
        log.error("handleTokenValidationException", e);
        return errorResponse(ErrorResponse.of(e.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e) {
        log.error("handleUnauthorizedException", e);
        return errorResponse(ErrorResponse.of(e.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(BizException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BizException e) {
        log.error("handleBusinessException", e);
        return errorResponse(ErrorResponse.of(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ResponseEntity<ErrorResponse> errorResponse(final ErrorResponse response){
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
