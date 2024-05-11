package com.cybozu.labs.langdetect.constant.enums;

/**
 * @author Nakatani Shuyo
 */
public enum ErrorCode {
    /**
     * 报错信息
     **/
    NoTextError,
    FormatError,
    FileLoadError,
    DuplicateLangError,
    NeedLoadProfileError,
    CantDetectError,
    CantOpenTrainData,
    TrainDataFormatError,
    InitParamError
}
