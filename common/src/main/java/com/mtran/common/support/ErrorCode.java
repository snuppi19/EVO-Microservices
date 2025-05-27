package com.mtran.common.support;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Token key invalid ", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    EMAIL_EXISTS(1008, "Email already exists", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND(1009, "Email not found", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1010, "Email is invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1011, "Password is invalid", HttpStatus.BAD_REQUEST),
    ID_INVALID(1012, "Id is invalid", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1013, "User not found", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_NOT_FOUND(1014, "Refresh token is not found", HttpStatus.BAD_REQUEST),
    USE_KEYCLOAK_LOGIN(1015, "Use keycloak login", HttpStatus.BAD_REQUEST),
    LOGOUT_FAILED(1016, "Logout failed", HttpStatus.BAD_REQUEST),
    TOKEN_GENERATION_FAILED(1017, "Token generation failed", HttpStatus.BAD_REQUEST),
    CHANGE_PASSWORD_FAILED(1018,"CHANGE_PASSWORD_FAILED", HttpStatus.BAD_REQUEST),
    DELETE_USER_FAILED(1019,"DELETE_USER_FAILED", HttpStatus.BAD_REQUEST),
    CHANGE_ACTIVE_STATUS_FAILED(1020,"CHANGE_ACTIVE_STATUS_FAILED", HttpStatus.BAD_REQUEST),
    CANT_CALLBACK(1021,"CANT_CALLBACK", HttpStatus.BAD_REQUEST),
    REGISTER_FAILD(1022,"REGISTER_FAILED", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1023,"ROLE_NOT_FOUND", HttpStatus.BAD_REQUEST),
    USER_NOT_HAVE_ROLES(1024,"USER_NOT_HAVE_ROLES", HttpStatus.BAD_REQUEST),


    // Storage service
    FILE_NOT_FOUND(1000, "File is null", HttpStatus.BAD_REQUEST),
    FILE_SIZE_LIMIT_AT_10MB(1001, "File size limit at 10MB", HttpStatus.BAD_REQUEST),
    CREATE_DATE_INVALID(1002,"createAt date is invalid",HttpStatus.BAD_REQUEST),
    UPDATE_DATE_INVALID(1003,"updateAt date is invalid",HttpStatus.BAD_REQUEST),
    FILE_PRIVATE(1004,"File is private",HttpStatus.FORBIDDEN)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
