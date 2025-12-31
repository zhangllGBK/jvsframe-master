package com.jarveis.frame.config;

import com.jarveis.frame.util.CharacterUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类扫描器
 *
 * @author liuguojun
 * @since 2022-06-29
 */

public class ClassScanner {

    private static final Logger log = LoggerFactory.getLogger(ClassScanner.class);

    private static final Set<String> classSet = new HashSet<String>();

    static {
        addPackage("com.jarveis.frame");
    }

    /**
     * 添加要扫描的包
     *
     * @param scanPackage
     */
    public static void addPackage(String scanPackage) {
        if (StringUtils.isNotBlank(scanPackage)) {
            try {
                String scanPath = scanPackage.replace(".", "/");
                loadClassFiles(scanPath, scanPackage);
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * 返回扫描完后的class集合
     *
     * @return
     */
    public static Set<String> getClassSet(){
        return classSet;
    }

    /**
     * 获取classpath下的class文件
     *
     * @param classpath   运行环境的class路径
     * @param packageName 包名
     * @return
     * @throws IOException
     */
    private static void loadClassFiles(String classpath, String packageName) throws IOException {
        // 获取classpath路径
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(classpath);
        while (urls != null && urls.hasMoreElements()) {
            URL url = urls.nextElement();
            String protocol = url.getProtocol();
            // 读取文件路径
            if ("file".equals(protocol)) {
                File dir = new File(URLDecoder.decode(url.getPath(), CharacterUtil.UTF8));
                if (dir.isDirectory()) {
                    addClassFile(dir.getPath(), dir, packageName);
                } else {
                    throw new IllegalArgumentException("classpath必须是目录");
                }
            } else if ("jar".equals(protocol)) {
                String jarpath = StringUtils.split(url.getPath(), '!')[0].replace("file:", StringUtils.EMPTY);
                File dir = new File(URLDecoder.decode(jarpath, CharacterUtil.UTF8));
                // 如果是jar文件
                addJarFile(dir.getPath(), dir);
            }
        }
    }

    /**
     * 递归把class封装到set集合里
     *
     * @param classpath 运行环境的class路径
     * @param dir       class目录或文件
     * @param packageName 包名
     */
    private static void addClassFile(String classpath, File dir, String packageName) {

        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                addClassFile(classpath, file, packageName);
            }
        } else if (dir.getName().endsWith(".class")) {
            String name = dir.getPath();

            if (packageName == null) {
                name = name.substring(classpath.length() + 1, name.length() - 6).replace(File.separator, ".");
            } else {
                name = name.substring(0, name.length() - 6).replace(File.separator, ".");
                name = name.substring(name.indexOf(packageName));
            }
            classSet.add(name);
            if (log.isDebugEnabled()) {
                log.debug("class=" + name);
            }
        }
    }

    /**
     * 把jar文件中的class封装到set集合里
     *
     * @param classpath 运行环境的class路径
     * @param file      jar文件
     */
    private static void addJarFile(String classpath, File file) throws IOException {
        // 如果是jar文件
        JarFile jar = new JarFile(file);
        Enumeration<JarEntry> en = jar.entries();
        while (en.hasMoreElements()) {
            JarEntry je = en.nextElement();
            String name = je.getName();
            if (name.endsWith(".class")) {
                String clazz = name.substring(0, name.length() - 6).replaceAll("/", ".");
                classSet.add(clazz);
                if (log.isDebugEnabled()) {
                    log.debug("class=" + clazz);
                }
            }
        }
    }

}
