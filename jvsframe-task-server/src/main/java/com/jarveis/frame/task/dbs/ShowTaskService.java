package com.jarveis.frame.task.dbs;

import com.jarveis.frame.dbs.Service;
import com.jarveis.frame.dbs.ServiceCode;
import com.jarveis.frame.dbs.ant.Function;
import com.jarveis.frame.task.TaskCache;
import com.jarveis.frame.task.TaskInfo;
import com.jarveis.frame.task.bean.TaskConfig;
import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.Param;
import com.jarveis.frame.util.Resource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 发布任务展示
 *
 * @author zhangll
 * @since 2020-12-23
 */
@Function(code = ServiceCode.TASK_PUBLISH_SHOW_SERVICE)
public class ShowTaskService implements Service {

    private static final Logger log = LoggerFactory.getLogger(ShowTaskService.class);

    @Override
    public Param callService(Param in) {
        Param out = null;

        try {
            if (log.isDebugEnabled()) {
                log.debug(in.toXmlString());
            }
            out = new Param(Param.RESP);
            // 所有发布任务
            Map taskMap = TaskCache.getTaskInfoMap();
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append("<html>\n");
            stringBuffer.append(" <head> \n").append(processScript()).append(" </head>\n");
            stringBuffer.append(" <body> \n");
            stringBuffer.append(parseNode(TaskCache.listTaskConfig()));
            stringBuffer.append(parseTaskInfo(taskMap));
            stringBuffer.append(parseTaskNode());
            stringBuffer.append(" </body>\n");
            stringBuffer.append("</html>");
            out.getBody().addCDATA(stringBuffer.toString());
            out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_SUCCESS);
            return out;

        } catch (Exception ex) {
            if (out != null) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
            }
            log.error(ex.getMessage(), ex);
        }
        return out;
    }

    private String parseTaskNode() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Map<String, List<Long>>> tasknodeMap = TaskCache.getTasknodeMap();
        if (tasknodeMap.isEmpty()) {
            return "";
        }
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("<table border=\"1\"> \n");
        stringBuffer.append("<tbody>\n");
        stringBuffer.append("<tr>\n");
        stringBuffer.append("<td>任务编号</td> \n");
        stringBuffer.append("<td>node编号</td> \n");
        stringBuffer.append("<td>node执行次数</td> \n");
        stringBuffer.append("<td>node最后执行时间</td> \n");
        stringBuffer.append("<td>操作</td> \n");
        stringBuffer.append("</tr>\n");

        for (String code : tasknodeMap.keySet()) {
            stringBuffer.append("<tr>\n");

            Map<String, List<Long>> map = tasknodeMap.get(code);
            stringBuffer.append("<td rowspan=").append(map.size()).append(">").append(code).append("</td> \n");
            int i = 0;
            if (map != null && !map.isEmpty()) {
                for (String node : map.keySet()) {
                    List<Long> list = map.get(node);
                    if (i > 0) {
                        stringBuffer.append("<tr>\n");
                    }
                    stringBuffer.append("<td>").append(node).append("</td> \n");
                    stringBuffer.append("<td>").append(list.size()).append("</td> \n");
                    stringBuffer.append("<td>").append(list.size() > 0 ? sdf.format(new Date(list.get(0))) : "").append("</td> \n");
                    stringBuffer.append("<td align=\"center\"><input type=\"button\" value=\"全部执行时间\" class=\"showlist\"  data-value=\"" + code + "," + node + "\"  >");
                    stringBuffer.append("<textarea style='display:none;'>");
                    String time = sdf.format(new Date(list.get(0)));
                    stringBuffer.append(time);
                    for (int j = 1; j < list.size(); j++) {
                        time = sdf.format(new Date(list.get(j)));
                        stringBuffer.append("\n").append(time);
                    }
                    stringBuffer.append("</textarea>");
                    stringBuffer.append("</td>\n");

                    if (i > 0) {
                        stringBuffer.append("</tr>\n");
                    }
                    i = 1;
                }

            }

            stringBuffer.append("</tr>\n");
        }


        return stringBuffer.toString();
    }

    private String parseTaskInfo(Map<String, TaskInfo> taskMap) {
        if (taskMap.isEmpty()) {
            return "";
        }
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("<table border=\"1\"> \n");
        stringBuffer.append("<tbody>\n");
        stringBuffer.append("<tr>\n");
        stringBuffer.append(parseTaskTh());
        stringBuffer.append("</tr>\n");
        for (TaskInfo taskInfo : taskMap.values()) {
            String code = taskInfo.getCode();
            String sleepTime = taskInfo.getSleepTime();
            String status = taskInfo.getStatus().toString();
            long runTime = taskInfo.getRunTime();
            String clazz = taskInfo.getClazz();
            String param = taskInfo.getParam();
            String[] nodes = taskInfo.getNodes();
            Map<String, Integer> nodetype = taskInfo.getNodeType();
            stringBuffer.append("<tr>\n");
            stringBuffer.append("<td>").append(code).append("</td> \n");
            stringBuffer.append("<td>").append(sleepTime).append("</td> \n");
            stringBuffer.append("<td>").append(status).append("</td> \n");
            stringBuffer.append("<td>").append(runTime).append("</td> \n");
            stringBuffer.append("<td>").append(param).append("</td> \n");
            stringBuffer.append("<td>").append(clazz).append("</td> \n");
            String nodetypehtml = "";
            String nodetypehtml2 = "";
            String nodetypehtml3 = "";
            for (String node : nodetype.keySet()) {
                nodetypehtml += ",<span>" + node + "</span>";
                if (nodetype.get(node) == 1) {
                    nodetypehtml2 += ",<span style=\"color:red\">" + node + "</span>";
                } else if (nodetype.get(node) == 2) {
                    nodetypehtml3 += ",<span style=\"color:green\">" + node + "</span>";
                }
            }
            if (!StringUtils.isEmpty(nodetypehtml)) {
                nodetypehtml = nodetypehtml.substring(1);
            }
            if (!StringUtils.isEmpty(nodetypehtml2)) {
                nodetypehtml2 = nodetypehtml2.substring(1);
            }
            if (!StringUtils.isEmpty(nodetypehtml3)) {
                nodetypehtml3 = nodetypehtml3.substring(1);
            }
            stringBuffer.append("<td>").append(nodetypehtml).append("</td>\n");
            stringBuffer.append("<td>").append(nodetypehtml2).append("</td>\n");
            stringBuffer.append("<td>").append(nodetypehtml3).append("</td>\n");
            stringBuffer.append("<td align=\"center\"><input type=\"button\" value=\"删除\" class=\"delTask\"  data-value=\"" + code + "\"  ></td>\n");
            stringBuffer.append("</tr>\n");
        }
        stringBuffer.append("<tr>");
        stringBuffer.append("<td colspan=\"10\" align=\"center\"><input type=\"button\" value=\"添加任务\" class=\"addTask\" ></td>\n");
        stringBuffer.append("</tr>\n");
        stringBuffer.append("</tbody>\n");
        stringBuffer.append("</table>\n");
        return stringBuffer.toString();
    }

    /**
     * 任务表头
     *
     * @return
     */
    public String parseTaskTh() {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("<td>任务编号</td> \n");
        stringBuffer.append("<td>任务执行完成后休息的时间</td> \n");
        stringBuffer.append("<td>任务调度状态</td> \n");
        stringBuffer.append("<td>下次运行时间</td> \n");
        stringBuffer.append("<td>执行参数</td> \n");
        stringBuffer.append("<td>执行处理类</td> \n");
        stringBuffer.append("<td>已分配node</td> \n");
        stringBuffer.append("<td>执行中node</td> \n");
        stringBuffer.append("<td>已执行node</td> \n");
        stringBuffer.append("<td>操作</td> \n");
        return stringBuffer.toString();
    }

    private String parseNode(Collection<TaskConfig> listTaskConfig) {
        if (listTaskConfig.isEmpty()) {
            return "";
        }
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("<table border=\"1\"> \n");
        stringBuffer.append("<tbody>\n");
        stringBuffer.append("<tr>\n");
        stringBuffer.append(parseTaskNodeTh());
        stringBuffer.append("</tr>\n");
        for (TaskConfig taskConfig : listTaskConfig) {
            if (taskConfig.getState()) {
                String dbsmachine = taskConfig.getDbsmachine(); // node编号
                String dbslocal = taskConfig.getDbslocal(); // node地址
                int remain = taskConfig.getRemain(); // 空闲线程
                int minSize = taskConfig.getMinSize(); // 连接池最小线程数
                int maxSize = taskConfig.getMaxSize(); // 连接池最大线程数
                String tasksType = taskConfig.getTaskType(); // 支持任务类型
                stringBuffer.append("<tr>\n");
                stringBuffer.append("<td>").append(dbsmachine).append("</td> \n");
                stringBuffer.append("<td>").append(dbslocal).append("</td> \n");
                stringBuffer.append("<td>").append(remain).append("</td> \n");
                stringBuffer.append("<td>").append(minSize).append("</td> \n");
                stringBuffer.append("<td>").append(maxSize).append("</td> \n");
                stringBuffer.append("<td>").append(tasksType).append("</td> \n");
                stringBuffer.append("</tr>\n");
            }
        }
        stringBuffer.append("   </tbody>\n");
        stringBuffer.append("  </table>  \n");
        return stringBuffer.toString();
    }

    /**
     * 任务表头
     *
     * @return
     */
    public String parseTaskNodeTh() {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("<td>node服务器编号</td> \n");
        stringBuffer.append("<td>node服务器地址</td> \n");
        stringBuffer.append("<td>node可用线程</td> \n");
        stringBuffer.append("<td>node最小初始化线程</td> \n");
        stringBuffer.append("<td>node最大线程数</td> \n");
        stringBuffer.append("<td>node支持任务类型</td> \n");
        return stringBuffer.toString();
    }

    /**
     * 处理脚本
     *
     * @return 脚本信息
     */
    private String processScript() throws Exception {
        StringBuilder script = new StringBuilder();
        script.append("<script src=\"https://cdn.bootcdn.net/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>");
        script.append("<script type=\"text/javascript\">");
        script.append(IOUtils.toString(Resource.getStream("task.js"), CharacterUtil.UTF8));
        script.append("</script>");
        return script.toString();
    }
}

