package com.cybozu.labs.langdetect.exception;

import com.cybozu.labs.langdetect.constant.enums.ErrorCode;
import lombok.Getter;

/**
 * @author Nakatani Shuyo
 *
 */
@Getter
public class LangDetectException extends Exception {
    private static final long serialVersionUID = -5197285268097854086L;
    /** 错误码 **/
    private final ErrorCode code;

    /**
     * @param code 错误码
     * @param message 错误信息
     */
    public LangDetectException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }
}
