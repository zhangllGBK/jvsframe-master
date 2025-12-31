package com.jarveis.frame.task;

import com.jarveis.frame.bean.BeanUtil;
import com.jarveis.frame.config.ConfigParser;
import com.jarveis.frame.config.ModuleParser;
import com.jarveis.frame.task.bean.TaskConfig;
import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

/**
 * 计划后台程序
 *
 * @author liuguojun
 */
@ModuleParser
public class TaskParser implements ConfigParser {

    private static final Logger log = LoggerFactory.getLogger(TaskParser.class);

    public static final String TASK_CONFIG = "task.xml";

    public void parse() {
        parse(TASK_CONFIG);
    }

    /**
     * 初始化操作
     *
     * @param filePath 配置文件
     */
    public void parse(String filePath) {
        // 读取配置文件
        Document document;
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                document = Jsoup.parse(new FileInputStream(file), CharacterUtil.UTF8, "", Parser.xmlParser());
            } else {
                document = Jsoup.parse(Resource.getStream(filePath), CharacterUtil.UTF8, "", Parser.xmlParser());
            }
            this.initTaskPool(document);
            this.initTasks(document);
            TaskDaemon.getInstance().start();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 初始化任务池
     *
     * @param document 文档对象
     */
    private void initTaskPool(Document document) {
        String taskMachine = System.getProperty("task_machine"); // 节点机器号
        if (StringUtils.isEmpty(taskMachine)) {
            taskMachine = "taskMachine-001";
        }
        int minSize = 5, maxSize = 100;
        String taskType = StringUtils.EMPTY; //节点支持的任务类型
        List<Element> pps = document.select("taskConfig > taskPool > property");
        for (Element pp : pps) {
            String name = pp.attr("name");
            String value = pp.attr("value");
            if ("minSize".equals(name)) {
                minSize = NumberUtils.toInt(value, 5);
            } else if ("maxSize".equals(name)) {
                maxSize = NumberUtils.toInt(value, 100);
            } else if ("taskType".equals(name)) {
                taskType = value;
            }
        }
        // 当前节点的配置信息
        TaskConfig taskConfig = new TaskConfig(taskMachine, "", maxSize, taskType, minSize, maxSize);
        TaskCache.setTaskConfig(taskConfig);

        TaskDaemon taskDaemon = TaskDaemon.getInstance();
        taskDaemon.setTaskPool(minSize, maxSize);
    }

    /**
     * 初始化任务
     *
     * @param document 文档对象
     */
    private void initTasks(Document document) {
        List<Element> ts = document.select("taskConfig > tasks > task");

        for (Iterator<Element> it = ts.iterator(); it.hasNext(); ) {
            try {
                Element t = it.next();
                String code = t.attr("code"); // 编码
                String time = t.attr("time"); // 任务执行完后的休息时间
                String param = t.attr("param"); // 任务执行时需要的参数
                String nodes = t.attr("nodes"); // 远程服务器编号
                TaskInfo taskInfo = new TaskInfo(code, time, param);
                taskInfo.setStatus(0L, ITask.Status.Init); // 初始化任务执行状态
                taskInfo.setNodes(StringUtils.split(nodes, "|"));
                String clazz = t.attr("class");
                if (StringUtils.isEmpty(clazz)) {
                    continue;
                }
                taskInfo.setClazz(clazz);

                // 创建任务
                Object object = BeanUtil.newInstance(clazz);
                if (object instanceof ITask) {
                    ITask task = (ITask) object;
                    task.setTaskInfo(taskInfo);
                    TaskDaemon.getInstance().addTask(task);
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

}
