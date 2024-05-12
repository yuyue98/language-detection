package com.cybozu.labs.langdetect.util;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Nakatani Shuyo
 *
 */
public class TagExtractorTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

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
     * Test method for {@link com.cybozu.labs.langdetect.util.TagExtractor#TagExtractor(java.lang.String, int)}.
     */
    @Test
    final void testTagExtractor() {
        TagExtractor extractor = new TagExtractor(null, 0);
        assertEquals(extractor.target_, null);
        assertEquals(extractor.threshold_, 0);

        TagExtractor extractor2 = new TagExtractor("abstract", 10);
        assertEquals(extractor2.target_, "abstract");
        assertEquals(extractor2.threshold_, 10);
}

    /**
     * Test method for {@link com.cybozu.labs.langdetect.util.TagExtractor#setTag(java.lang.String)}.
     */
    @Test
    final void testSetTag() {
        TagExtractor extractor = new TagExtractor(null, 0);
        extractor.setTag("");
        assertEquals(extractor.tag_, "");
        extractor.setTag(null);
        assertEquals(extractor.tag_, null);
    }

    /**
     * Test method for {@link TagExtractor#add(String)}.
     */
    @Test
    final void testAdd() {
        TagExtractor extractor = new TagExtractor(null, 0);
        extractor.add("");
        extractor.add(null);    // ignore
    }

    /**
     * {@link com.cybozu.labs.langdetect.util.TagExtractor}
     * Test method for {@link TagExtractor#closeTag()}.
     */
    @Test
    final void testCloseTag() {
        TagExtractor extractor = new TagExtractor(null, 0);
        extractor.closeTag();    // ignore
    }

    
    /**
     * Scenario Test of extracting &lt;abstract&gt; tag from Wikipedia database.
     */
    @Test
    final void testNormalScenario() {
        TagExtractor extractor = new TagExtractor("abstract", 10);
        assertEquals(extractor.count(), 0);

        LangProfile profile = new LangProfile("en");

        // normal
        extractor.setTag("abstract");
        extractor.add("This is a sample text.");
        profile.update(extractor.closeTag());
        assertEquals(extractor.count(), 1);
        assertEquals(profile.getN_words()[0], 17);  // Thisisasampletext
        assertEquals(profile.getN_words()[1], 22);  // _T, Th, hi, ...
        assertEquals(profile.getN_words()[2], 17);  // _Th, Thi, his, ...

        // too short
        extractor.setTag("abstract");
        extractor.add("sample");
        profile.update(extractor.closeTag());
        assertEquals(extractor.count(), 1);

        // other tags
        extractor.setTag("div");
        extractor.add("This is a sample text which is enough long.");
        profile.update(extractor.closeTag());
        assertEquals(extractor.count(), 1);
    }

    /**
     * Test method for {@link com.cybozu.labs.langdetect.util.TagExtractor#clear()}.
     */
    @Test
    final void testClear() {
        TagExtractor extractor = new TagExtractor("abstract", 10);
        extractor.setTag("abstract");
        extractor.add("This is a sample text.");
        assertEquals(extractor.buf_.toString(), "This is a sample text.");
        assertEquals(extractor.tag_, "abstract");
        extractor.clear();
        assertEquals(extractor.buf_.toString(), "");
        assertEquals(extractor.tag_, null);
    }


}
