package com.cybozu.labs.langdetect;

/**
 * @author Nakatani Shuyo
 */
enum ErrorCode {
    /** 报错信息 **/
    NoTextError, FormatError, FileLoadError, DuplicateLangError, NeedLoadProfileError, CantDetectError, CantOpenTrainData, TrainDataFormatError, InitParamError
}

/**
 * @author Nakatani Shuyo
 *
 */
public class LangDetectException extends Exception {
    private static final long serialVersionUID = 1L;
    private final ErrorCode code;
    

    /**
     * @param code 错误码
     * @param message 错误信息
     */
    public LangDetectException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * @return the error code
     */
    public ErrorCode getCode() {
        return code;
    }
}
