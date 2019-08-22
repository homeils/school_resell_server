package com.renoside.schoolresell.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 服务器拒绝响应请求
 */
@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "拒绝响应请求")
public class ForbiddenException extends RuntimeException {
}
