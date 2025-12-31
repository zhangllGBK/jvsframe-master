package com.jarveis.frame.task;

import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.Resource;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * 风控配置格式化
 *
 * @author liuguojun
 * @since 2020-06-17
 */
public class TaskFormat {

    private static final Logger log = LoggerFactory.getLogger(TaskFormat.class);

    public static final String CONFIG_PATH = "task.xml";
    public static void main(String[] args) {
        new TaskFormat().format();
    }

    public void format() {
        format(CONFIG_PATH);
    }

    public void format(String filePath) {
        // 读取配置文件
        Document document;
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                file = new File(Resource.getURL(filePath).getFile());
            }
            document = Jsoup.parse(new FileInputStream(file), CharacterUtil.UTF8, "", Parser.xmlParser());
            this.formatTask(document);
            FileOutputStream fos = new FileOutputStream(file, false);
            OutputStreamWriter osw = new OutputStreamWriter(fos, CharacterUtil.UTF8);
            osw.write(document.html());
            osw.close();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void formatTask(Document document) {
        document.select("taskConfig > tasks > task").remove();
        Element taskElement = document.selectFirst("tasks");
        Map<String, TaskInfo> map = TaskCache.getTaskInfoMap();
        for (TaskInfo taskInfo : map.values()) {
            Element task = taskElement.appendElement("task");
            task.attr("code", taskInfo.getCode());
            task.attr("time", taskInfo.getSleepTime());
            task.attr("class", taskInfo.getClazz());
            task.attr("param", taskInfo.getParam());
            task.attr("nodes", StringUtils.join(taskInfo.getNodes(), "|"));
        }
    }

}
