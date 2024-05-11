package com.cybozu.labs.langdetect.util;

import lombok.Getter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * {@link LangProfile} is a Language Profile Class.
 * Users don't use this class directly.
 * 
 * @author Nakatani Shuyo
 */
@Getter
public class LangProfile {
    private static final int MINIMUM_FREQ = 2;
    private static final int LESS_FREQ_RATIO = 100000;

    private String name = null;
    private final Map<String, Integer> freq = new HashMap<>();
    private final int[] nWords = new int[NGram.N_GRAM];

    /**
     * Constructor for JSONIC 
     */
    public LangProfile() {}

    /**
     * Normal Constructor
     * @param name language name
     */
    public LangProfile(String name) {
        this.name = name;
    }
    
    /**
     * Add n-gram to profile
     * @param gram gram
     */
    public void add(String gram) {
        if (this.getName() == null || gram == null) {
            return;   // Illegal
        }
        int len = gram.length();
        if (len < 1 || len > NGram.N_GRAM) {
            return;  // Illegal
        }
        ++this.getNWords()[len - 1];
        if (this.getFreq().containsKey(gram)) {
            this.getFreq().put(gram, this.getFreq().get(gram) + 1);
        } else {
            this.getFreq().put(gram, 1);
        }
    }

    /**
     * Eliminate below less frequency n-grams and noise Latin alphabets
     */
    public void omitLessFreq() {
        if (this.getName() == null) {
            return;   // Illegal
        }
        int threshold = this.getNWords()[0] / LESS_FREQ_RATIO;
        if (threshold < MINIMUM_FREQ) {
            threshold = MINIMUM_FREQ;
        }
        
        Set<String> keys = this.getFreq().keySet();
        int roman = 0;
        for(Iterator<String> i = keys.iterator(); i.hasNext(); ){
            String key = i.next();
            int count = this.getFreq().get(key);
            if (count <= threshold) {
                this.getNWords()[key.length()-1] -= count;
                i.remove();
            } else {
                if (key.matches("^[A-Za-z]$")) {
                    roman += count;
                }
            }
        }

        // roman check
        if (roman < this.getNWords()[0] / 3) {
            Set<String> keys2 = this.getFreq().keySet();
            for(Iterator<String> i = keys2.iterator(); i.hasNext(); ){
                String key = i.next();
                if (key.matches(".*[A-Za-z].*")) {
                    this.getNWords()[key.length()-1] -= this.getFreq().get(key);
                    i.remove();
                }
            }
            
        }
    }

    /**
     * Update the language profile with (fragmented) text.
     * Extract n-grams from text and add their frequency into the profile.
     * @param text (fragmented) text to extract n-grams
     */
    public void update(String text) {
        if (text == null) {
            return;
        }
        text = NGram.normalize_vi(text);
        NGram gram = new NGram();
        for(int i=0; i<text.length(); ++i) {
            gram.addChar(text.charAt(i));
            for(int n=1; n<=NGram.N_GRAM; ++n) {
                add(gram.get(n));
            }
        }
    }
}
