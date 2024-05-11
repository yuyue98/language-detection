package com.cybozu.labs.langdetect;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * {@link Language} is to store the detected language.
 * {@link Detector#getProbabilities()} returns an {@link ArrayList} of {@link Language}s.
 *  
 * @see Detector#getProbabilities()
 * @author Nakatani Shuyo
 *
 */
@Getter
@Setter
public class Language {
    private String lang;
    private double prob;

    public Language(String lang, double prob) {
        this.lang = lang;
        this.prob = prob;
    }

    @Override
    public String toString() {
        if (lang==null) {
            return "";
        }
        return lang + ":" + prob;
    }
}
