package com.cybozu.labs.langdetect.util;

import com.cybozu.labs.langdetect.constant.enums.DefaultCorpusEnum;
import lombok.extern.slf4j.Slf4j;
import net.arnx.jsonic.JSON;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 资源读取工具类
 *
 * @author yuyue
 * @date 2024-05-11 19:46:37
 */
@Slf4j
public class LdResourceUtil {

    public static final String HIDDEN_FILE_PREFIX = ".";

    private LdResourceUtil() {}

    /** 当前类 **/
    private static final Class<LdResourceUtil> CLAZZ = LdResourceUtil.class;
    /** 当前路径 **/
    public static final String PATHNAME = LdResourceUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    /**
     * 根据资源文件路径，获取资源文件URL
     * @param name 资源文件路径
     * @return 资源文件URL
     */
    public static URL getResource(String name) {
        return CLAZZ.getResource(name);
    }

    /**
     * 根据资源文件路径，获取资源文件流
     * @param name 资源文件路径
     * @return 资源文件流
     */
    public static InputStream getResourceAsStream(String name) {
        return CLAZZ.getResourceAsStream(name);
    }

    /**
     * 解析并关闭语料文件流
     * @param in 语料文件流
     * @return 语料对象
     */
    public static LangProfile toLangProfile(InputStream in) {
        if (null == in) {
            return null;
        }
        LangProfile profile = null;
        try {
            profile = JSON.decode(in, LangProfile.class);
        } catch (Exception e) {
            log.info("解析语料文件失败", e);
        } finally {
            LdIoUtil.close(in);
        }
        return profile;
    }

    /**
     * 获得文件输入流
     * @param file 文件
     * @return 输入流
     */
    public static BufferedInputStream getInputStream(File file) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            log.error(String.format("无法找到文件: %s", file.getAbsolutePath()), e);
            return null;
        }
        return new BufferedInputStream(fis);
    }

    /**
     * 解压zip获取zip下的某个文件流
     * @param in  zip对应的输入流
     * @return  zip下文件对应输入流
     */
    public static InputStream unZip(InputStream in) {
        if (null == in) {
            return null;
        }
        byte[] bytes = new byte[1024];
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            while(true) {
                int len = in.read(bytes);
                if (len <= 0) {
                    break;
                }
                bos.write(bytes);
            }
            bos.flush();
            return new ByteArrayInputStream(bos.toByteArray());
        } catch (IOException e) {
            log.info("解压zip文件失败", e);
            return null;
        } finally {
            LdIoUtil.close(in);
        }
    }

    /**
     * 递归遍历目录以及子目录中的所有文件
     * @param file 起始目录
     * @param list 文件列表
     * @return 文件列表
     */
    public static List<File> loopFiles(File file, List<File> list) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File f : files) {
                    loopFiles(f, list);
                }
            }
        } else {
            list.add(file);
        }
        return list;
    }

    /**
     * 判断当前路径类型
     * @return true，目录；false，jar包
     */
    public static boolean judgePathType() {
        log.info("当前路径：{}", PATHNAME);
        return new File(PATHNAME).isDirectory();
    }

    /**
     * 获取默认语料库所有语料文件流
     * filename ==> 文件流
     * @return 语料文件流列表
     */
    public static Map<String, LangProfile> getCorpusIoList() {
        return getCorpusIoList(DefaultCorpusEnum.defaultValue());
    }

    /**
     * 获取默认语料库所有语料文件流
     * filename ==> 文件流
     * @param corpus 默认语料库枚举
     * @return 语料文件流列表
     */
    public static Map<String, LangProfile> getCorpusIoList(DefaultCorpusEnum corpus) {
        if (judgePathType()) {
            return getCorpusIoListForDirectory(corpus);
        } else {
            return getCorpusIoListForJar(corpus);
        }
    }

    /**
     * 获取默认语料库所有语料文件路径
     * @return 语料文件路径列表
     */
    public static Map<String, LangProfile> getCorpusIoListForDirectory(DefaultCorpusEnum corpus) {
        if (null == corpus) {
            corpus = DefaultCorpusEnum.defaultValue();
        }
        File file = new File(PATHNAME, corpus.getPath());
        List<File> files = loopFiles(file, new ArrayList<>());
        Map<String, LangProfile> map = new HashMap<>(files.size());
        files.stream()
                .filter(f -> null != f && !f.isDirectory() && !f.getName().startsWith(HIDDEN_FILE_PREFIX))
                .forEach(f -> {
                    InputStream is = getInputStream(f);
                    LangProfile profile = toLangProfile(is);
                    if (null != profile) {
                        map.put(f.toString(), profile);
                    }
                });
        return map;
    }

    /**
     * 从jar包获取默认语料库所有语料
     * @param corpus 默认语料库枚举
     * @return 语料文件流列表
     */
    public static Map<String, LangProfile> getCorpusIoListForJar(DefaultCorpusEnum corpus) {
        if (null == corpus) {
            corpus = DefaultCorpusEnum.defaultValue();
        }
        Map<String, LangProfile> map = new HashMap<>(100);
        // 使用JarFile打开jar文件
        try (JarFile jar = new JarFile(PATHNAME)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                // 获取JarEntry对象
                JarEntry entry = entries.nextElement();
                // 获取当前JarEntry对象的路径+文件名
                String name = entry.getName();
                if (name.startsWith(corpus.getPath()) && !entry.isDirectory() && !Paths.get(entry.getName()).getFileName().startsWith(HIDDEN_FILE_PREFIX)) {
                    InputStream is = jar.getInputStream(entry);
                    LangProfile profile = toLangProfile(is);
                    if (null != profile) {
                        map.put(name, profile);
                    }
                }
            }
        } catch (IOException e) {
            log.error("从jar包获取默认语料库所有语料文件流失败", e);
        }
        return map;
    }
}
