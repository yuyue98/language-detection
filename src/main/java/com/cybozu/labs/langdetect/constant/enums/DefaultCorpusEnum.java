package com.cybozu.labs.langdetect.constant.enums;

import lombok.Getter;

/**
 * 默认语料库枚举类
 *
 * @author yuyue
 * @date 2024-05-11 21:28:13
 */
@Getter
public enum DefaultCorpusEnum {
    /**
     * 默认语料库
     */
    TYPE_1("profiles"),
    TYPE_2("profiles.sm"),
    ;

    private final String path;

    DefaultCorpusEnum(String path) {
        this.path = path;
    }

    /**
     * 获取默认枚举
     * @return 默认枚举
     */
    public static DefaultCorpusEnum defaultValue() {
        return TYPE_2;
    }
}
