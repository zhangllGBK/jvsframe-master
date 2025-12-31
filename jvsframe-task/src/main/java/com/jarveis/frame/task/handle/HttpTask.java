package com.jarveis.frame.task.handle;

import com.jarveis.frame.task.ITask;
import com.jarveis.frame.util.HttpUtil;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * http请求任务
 * <pre>
 *     param = {
 *         "url": "",
 *         "getType": "post|get",
 *         "data": {
 *             "key": "value"
 *         }
 *     }
 * </pre>
 *
 * @author liuguojun
 * @since
 */
public class HttpTask extends ITask {

    private static final Logger log = LoggerFactory.getLogger(HttpTask.class);

    // String param = "{\"url\":\"http://192.168.1.170:9999/dbs.service\",\"getType\":\"post\",\"params\":{\"id\":\"123\",\"name\":\"123\",\"age\":\"123\"}}";
    @Override
    public void execute() {
        try {
            String param = this.getTaskInfo().getParam();
            if(StringUtils.isEmpty(param)){
                log.info("param任务执行参数为空");
                return;
            }
            Param in =new Param(param);
            // http 请求地址
            String url = in.getBody().getString("@url");
            if(StringUtils.isEmpty(url)){
                log.info("url参数为空");
                return;
            }
            // 请求方式
            String getType = in.getBody().getString("@getType");
            // 请求地址参数
            Param child = in.getBody().getChild("data");
            Map map = new HashMap();
            if(child!=null){
                map = child.getPropertys();
            }
            String result ="";
            if("get".equals(getType)){
                if(!map.isEmpty()){
                    url +="?";
                    for (Object key:map.keySet()) {
                        url+=key+"="+map.get(key);
                    }
                }
                result = HttpUtil.doGet(url);
            }else{
                result = HttpUtil.doPost(url, map);
            }

            if(!StringUtils.isEmpty(result)){
                log.debug("httptask fail;url="+url+";result="+result);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage(), ex);
        }
    }
}
