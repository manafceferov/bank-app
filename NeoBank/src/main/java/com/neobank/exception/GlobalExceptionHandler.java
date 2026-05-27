package com.neobank.exception;

import com.neobank.constant.ApiResponse;
import com.neobank.enums.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {

        String errorCode = ex.getMessage();
        Messages messageEnum;

        try {
            messageEnum = Messages.valueOf(errorCode);
        } catch (IllegalArgumentException e) {
            messageEnum = Messages.VALIDATION_ERROR;
        }

        HttpStatus status = getHttpStatus(messageEnum);
        String userMessage = getUserFriendlyMessage(messageEnum);

        return ResponseEntity.status(status)
                .body(new ApiResponse<>(false, null, userMessage));
    }

    private HttpStatus getHttpStatus(Messages msg) {
        return switch (msg) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case FORBIDDEN, CREDIT_NOT_APPROVED -> HttpStatus.FORBIDDEN;
            case INVALID_CREDENTIALS -> HttpStatus.UNAUTHORIZED;
            case ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case INSUFFICIENT_BALANCE, ACCOUNT_BLOCKED, CARD_BLOCKED,
                 DEPOSIT_ALREADY_CLOSED, TRANSFER_FAILED, VALIDATION_ERROR ->
                    HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private String getUserFriendlyMessage(Messages msg) {
        return switch (msg) {
            case NOT_FOUND -> "Məlumat tapılmadı";
            case FORBIDDEN -> "Bu əməliyyata icazəniz yoxdur";
            case INVALID_CREDENTIALS -> "Email və ya şifrə səhvdir";
            case ALREADY_EXISTS -> "Bu məlumat artıq mövcuddur";
            case INSUFFICIENT_BALANCE -> "Balansınız kifayət etmir";
            case ACCOUNT_BLOCKED -> "Hesab bloklanmışdır";
            case CARD_BLOCKED -> "Kart bloklanmışdır";
            case CREDIT_NOT_APPROVED -> "Kredit təsdiqlənməyib";
            case DEPOSIT_ALREADY_CLOSED -> "Depozit artıq bağlanıb";
            case TRANSFER_FAILED -> "Köçürmə uğursuz oldu";
            case VALIDATION_ERROR -> "Daxil edilən məlumatlar səhvdir";
            default -> "Xəta baş verdi";
        };
    }
}