package com.renoside.schoolresell.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 未授权错误异常
 */
@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "未授权的操作")
public class UnauthorizedException extends RuntimeException {
}
