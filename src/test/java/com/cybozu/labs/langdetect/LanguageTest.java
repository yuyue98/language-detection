package com.cybozu.labs.langdetect;

import com.cybozu.labs.langdetect.exception.LangDetectException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Nakatani Shuyo
 *
 */
class LanguageTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeEach
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterEach
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link com.cybozu.labs.langdetect.Language#Language(java.lang.String, double)}.
     */
    @Test
    final void testLanguage() {
        Language lang = new Language(null, 0);
        assertEquals(lang.getLang(), null);
        assertEquals(lang.getProb(), 0.0, 0.0001);
        assertEquals(lang.toString(), "");
        
        Language lang2 = new Language("en", 1.0);
        assertEquals(lang2.getLang(), "en");
        assertEquals(lang2.getProb(), 1.0, 0.0001);
        assertEquals(lang2.toString(), "en:1.0");
        
    }

    @Test
    final void testText() throws LangDetectException {
        String text = "hello";
        DetectorFactory.loadDefaultProfile();
        Detector detector = DetectorFactory.create();
        detector.append(text);
        String detect = detector.detect();
        System.out.println(detect);

        assertNotNull(detect);
    }

}
