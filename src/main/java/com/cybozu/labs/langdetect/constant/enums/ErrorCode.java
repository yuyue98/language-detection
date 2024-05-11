package com.cybozu.labs.langdetect.constant.enums;

/**
 * 错误码枚举类
 * @author Nakatani Shuyo
 */
public enum ErrorCode {
    /**
     * 错误码
     */
    NO_TEXT_ERROR,
    FORMAT_ERROR,
    FILE_LOAD_ERROR,
    DUPLICATE_LANG_ERROR,
    NEED_LOAD_PROFILE_ERROR,
    CANT_DETECT_ERROR,
    CANT_OPEN_TRAIN_DATA,
    TRAIN_DATA_FORMAT_ERROR,
    INIT_PARAM_ERROR
}
