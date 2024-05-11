package com.cybozu.labs.langdetect.exception;

import com.cybozu.labs.langdetect.constant.enums.ErrorCode;
import lombok.Getter;

/**
 * @author yuyue
 * @date 2024-05-11 19:38:33
 */
@Getter
public class LangDetectRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 6796342714668969550L;
    /** 错误码 **/
    private final ErrorCode code;

    /**
     * @param code 错误码
     * @param message 错误信息
     */
    public LangDetectRuntimeException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * @param code 错误码
     * @param cause 异常
     */
    public LangDetectRuntimeException(ErrorCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }
}
