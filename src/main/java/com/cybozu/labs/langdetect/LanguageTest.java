/**
 * 
 */
package com.cybozu.labs.langdetect;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Nakatani Shuyo
 *
 */
public class LanguageTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link com.cybozu.labs.langdetect.Language#Language(java.lang.String, double)}.
     */
    @Test
    public final void testLanguage() {
        Language lang = new Language(null, 0);
        assertEquals(lang.lang, null);
        assertEquals(lang.prob, 0.0, 0.0001);
        assertEquals(lang.toString(), "");
        
        Language lang2 = new Language("en", 1.0);
        assertEquals(lang2.lang, "en");
        assertEquals(lang2.prob, 1.0, 0.0001);
        assertEquals(lang2.toString(), "en:1.0");
        
    }

    @Test
    public final void testText() throws LangDetectException {
        String profileDirectory = "profiles.sm";
        String text = "hello";
        DetectorFactory.loadProfile(new File(profileDirectory));
        Detector detector = DetectorFactory.create();
        detector.append(text);
        String detect = detector.detect();

        assertNotNull(detect);
    }

}
