package com.cybozu.labs.langdetect;

import com.cybozu.labs.langdetect.constant.enums.DefaultCorpusEnum;
import com.cybozu.labs.langdetect.constant.enums.ErrorCode;
import com.cybozu.labs.langdetect.exception.LangDetectException;
import com.cybozu.labs.langdetect.exception.LangDetectRuntimeException;
import com.cybozu.labs.langdetect.util.LangProfile;
import com.cybozu.labs.langdetect.util.LdResourceUtil;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Language Detector Factory Class
 * 
 * This class manages an initialization and constructions of {@link Detector}. 
 * 
 * Before using language detection library, 
 * load profiles with {@link DetectorFactory#loadProfile(String)} method
 * and set initialization parameters.
 * 
 * When the language detection,
 * construct Detector instance via {@link DetectorFactory#create()}.
 * See also {@link Detector}'s sample code.
 * 
 * <ul>
 * <li>4x faster improvement based on Elmer Garduno's code. Thanks!</li>
 * </ul>
 * 
 * @see Detector
 * @author Nakatani Shuyo
 */
public class DetectorFactory {
    public HashMap<String, double[]> wordLangProbMap;
    public ArrayList<String> langlist;
    public Long seed = null;
    private DetectorFactory() {
        wordLangProbMap = new HashMap<String, double[]>();
        langlist = new ArrayList<String>();
    }

    private static final DetectorFactory INSTANCE = new DetectorFactory();

    /**
     * 加载默认语料库
     */
    public static void loadDefaultProfile() {
        loadDefaultProfile(DefaultCorpusEnum.defaultValue());
    }

    /**
     * 加载默认语料库
     * @param corpus 语料库枚举
     */
    public static void loadDefaultProfile(DefaultCorpusEnum corpus) {
        Map<String, LangProfile> map = LdResourceUtil.getCorpusIoList(corpus);
        int langsize = map.size();
        AtomicInteger index = new AtomicInteger();
        map.forEach((filename, profile) -> {
            try {
                // 跳过未获取到语料的文件
                if (null == profile) {
                    return;
                }
                addProfile(profile, index.get(), langsize);
                index.incrementAndGet();
            } catch (JSONException e) {
                throw new LangDetectRuntimeException(ErrorCode.FORMAT_ERROR, String.format("profile format error in '%s'", filename));
            } catch (LangDetectException e) {
                throw new LangDetectRuntimeException(e.getCode(), e);
            }
        });
    }

    /**
     * Load profiles from specified directory.
     * This method must be called once before language detection.
     *
     * @throws LangDetectException  Can't open profiles(error code = {@link ErrorCode#FILE_LOAD_ERROR})
     *                              or profile's format is wrong (error code = {@link ErrorCode#FORMAT_ERROR})
     */
    public static void loadProfile() throws LangDetectException {
        URL resource = DetectorFactory.class.getResource("/profiles");
        if (resource != null) {
            loadProfile(resource.getFile());
        } else {
            throw new LangDetectException(ErrorCode.NEED_LOAD_PROFILE_ERROR, "Not found profiles");
        }
    }

    /**
     * Load profiles from specified directory.
     * This method must be called once before language detection.
     *  
     * @param profileDirectory profile directory path
     * @throws LangDetectException  Can't open profiles(error code = {@link ErrorCode#FILE_LOAD_ERROR})
     *                              or profile's format is wrong (error code = {@link ErrorCode#FORMAT_ERROR})
     */
    public static void loadProfile(String profileDirectory) throws LangDetectException {
        loadProfile(new File(profileDirectory));
    }

    /**
     * Load profiles from specified directory.
     * This method must be called once before language detection.
     *  
     * @param profileDirectory profile directory path
     * @throws LangDetectException  Can't open profiles(error code = {@link ErrorCode#FILE_LOAD_ERROR})
     *                              or profile's format is wrong (error code = {@link ErrorCode#FORMAT_ERROR})
     */
    public static void loadProfile(File profileDirectory) throws LangDetectException {
        File[] listFiles = profileDirectory.listFiles();
        if (listFiles == null) {
            throw new LangDetectException(ErrorCode.NEED_LOAD_PROFILE_ERROR, "Not found profile: " + profileDirectory);
        }
            
        int langsize = listFiles.length, index = 0;
        for (File file: listFiles) {
            if (file.getName().startsWith(".") || !file.isFile()) {
                continue;
            }
            FileInputStream is = null;
            try {
                is = new FileInputStream(file);
                LangProfile profile = JSON.decode(is, LangProfile.class);
                addProfile(profile, index, langsize);
                ++index;
            } catch (JSONException e) {
                throw new LangDetectException(ErrorCode.FORMAT_ERROR, "profile format error in '" + file.getName() + "'");
            } catch (IOException e) {
                throw new LangDetectException(ErrorCode.FILE_LOAD_ERROR, "can't open '" + file.getName() + "'");
            } finally {
                try {
                    if (is!=null) {
                        is.close();
                    }
                } catch (IOException e) {}
            }
        }
    }

    /**
     * Load profiles from specified directory.
     * This method must be called once before language detection.
     *  
     * @param json_profiles profile directory path
     * @throws LangDetectException  Can't open profiles(error code = {@link ErrorCode#FILE_LOAD_ERROR})
     *                              or profile's format is wrong (error code = {@link ErrorCode#FORMAT_ERROR})
     */
    public static void loadProfile(List<String> json_profiles) throws LangDetectException {
        int index = 0;
        int langsize = json_profiles.size();
        if (langsize < 2) {
            throw new LangDetectException(ErrorCode.NEED_LOAD_PROFILE_ERROR, "Need more than 2 profiles");
        }
            
        for (String json: json_profiles) {
            try {
                LangProfile profile = JSON.decode(json, LangProfile.class);
                addProfile(profile, index, langsize);
                ++index;
            } catch (JSONException e) {
                throw new LangDetectException(ErrorCode.FORMAT_ERROR, "profile format error");
            }
        }
    }

    /**
     * @param profile
     * @param langsize 
     * @param index 
     * @throws LangDetectException 
     */
    static /* package scope */ void addProfile(LangProfile profile, int index, int langsize) throws LangDetectException {
        String lang = profile.name;
        if (INSTANCE.langlist.contains(lang)) {
            throw new LangDetectException(ErrorCode.DUPLICATE_LANG_ERROR, "duplicate the same language profile");
        }
        INSTANCE.langlist.add(lang);
        for (String word: profile.freq.keySet()) {
            if (!INSTANCE.wordLangProbMap.containsKey(word)) {
                INSTANCE.wordLangProbMap.put(word, new double[langsize]);
            }
            int length = word.length();
            if (length >= 1 && length <= 3) {
                double prob = profile.freq.get(word).doubleValue() / profile.n_words[length - 1];
                INSTANCE.wordLangProbMap.get(word)[index] = prob;
            }
        }
    }

    /**
     * Clear loaded language profiles (reinitialization to be available)
     */
    public static void clear() {
        INSTANCE.langlist.clear();
        INSTANCE.wordLangProbMap.clear();
    }

    /**
     * Construct Detector instance
     * 
     * @return Detector instance
     * @throws LangDetectException 
     */
    public static Detector create() throws LangDetectException {
        return createDetector();
    }

    /**
     * Construct Detector instance with smoothing parameter 
     * 
     * @param alpha smoothing parameter (default value = 0.5)
     * @return Detector instance
     * @throws LangDetectException 
     */
    public static Detector create(double alpha) throws LangDetectException {
        Detector detector = createDetector();
        detector.setAlpha(alpha);
        return detector;
    }

    private static Detector createDetector() throws LangDetectException {
        if (INSTANCE.langlist.size()==0) {
            throw new LangDetectException(ErrorCode.NEED_LOAD_PROFILE_ERROR, "need to load profiles");
        }
        Detector detector = new Detector(INSTANCE);
        return detector;
    }
    
    public static void setSeed(long seed) {
        INSTANCE.seed = seed;
    }
    
    public static final List<String> getLangList() {
        return Collections.unmodifiableList(INSTANCE.langlist);
    }
}
