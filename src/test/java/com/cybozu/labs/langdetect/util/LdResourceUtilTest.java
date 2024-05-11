package com.cybozu.labs.langdetect.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author yuyue
 * @date 2024-05-11 19:51:42
 */
@Slf4j
class LdResourceUtilTest {

    @Test
    void test() {
        try (InputStream is = LdResourceUtil.class.getResourceAsStream("/profiles")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8));
            while (reader.ready()) {
                String s = reader.readLine();
                System.out.println();
            }

        } catch (Exception e) {
            log.error("获取资源目录输入流失败", e);
        }

    }

    @Test
    void judgePathType() {
        assertTrue(LdResourceUtil.judgePathType());
    }

    @Test
    void getCorpusIoList() {
        Map<String, LangProfile> corpusIoList = LdResourceUtil.getCorpusIoList();
        assertNotNull(corpusIoList);
    }
}