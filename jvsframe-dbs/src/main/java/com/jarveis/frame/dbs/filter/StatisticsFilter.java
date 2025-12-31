package com.jarveis.frame.dbs.filter;

import com.jarveis.frame.dbs.ant.Interceptor;
import com.jarveis.frame.util.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 请求统计过滤器
 * <pre>
 *  1、统计每个服务的总请求
 *  2、统计每个服务的处理中的请求
 *  3、对服务的请求量，进行排序
 *  4、对处理中服务的请求量，进行排序
 * </pre>
 *
 * @author liuguojun
 * @since  2023-11-07
 */
@Interceptor(code = "statistics")
public class StatisticsFilter implements Filter{

    private static final Logger log = LoggerFactory.getLogger(StatisticsFilter.class);

    /* 所有的请求 */
    private final static Map<String, AtomicInteger> processes = new HashMap<>();

    /* 正在处理的请求 */
    private final static Map<String, AtomicInteger> processing = new HashMap<>();

    @Override
    public int init() {
        return 0;
    }

    @Override
    public int filter(Param in) {
        try {
            String tagName = in.getTagName();
            String funcId = in.getHead().getString(Param.LABEL_FUNCID);

            if (Param.REQ.equals(tagName)) {
                if (processes.get(funcId) == null) {
                    // 请求统计初始化
                    processes.put(funcId, new AtomicInteger(1));
                    processing.put(funcId, new AtomicInteger(1));
                } else {
                    // 请求统计计数递增
                    processes.get(funcId).incrementAndGet();
                    processing.get(funcId).incrementAndGet();
                }
            } else if (Param.RESP.equals(tagName)) {
                // 请求完成后，正在处理的请求递减
                processing.get(funcId).decrementAndGet();
            } else {

            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return 0;
    }

    /**
     * 获取所有请求数
     *
     * @return
     */
    public static int getProcesses(){

        return processes.values().stream().mapToInt(AtomicInteger::get).sum();
    }

    /**
     * 获取当前正在处理的请求数
     *
     * @return
     */
    public static int getProcessing() {

        return processing.values().stream().mapToInt(AtomicInteger::get).sum();
    }

    /**
     * 获取所有服务的请求排序（降序）
     *
     * @return
     */
    public static List<Map.Entry<String, AtomicInteger>> getProcessedSort() {
        Set<Map.Entry<String, AtomicInteger>> entrySet = processes.entrySet();

        return entrySet.stream().sorted(new Comparator<Map.Entry<String, AtomicInteger>>() {
            @Override
            public int compare(Map.Entry<String, AtomicInteger> o1, Map.Entry<String, AtomicInteger> o2) {
                return o2.getValue().get() - o1.getValue().get(); // 降序排序
            }
        }).collect(Collectors.toList());
    }

    /**
     * 获取处理中服务的请求排序（降序）
     *
     * @return
     */
    public static List<Map.Entry<String, AtomicInteger>> getProcessingSort() {
        Set<Map.Entry<String, AtomicInteger>> entrySet = processing.entrySet();

        return entrySet.stream().sorted(new Comparator<Map.Entry<String, AtomicInteger>>() {
            @Override
            public int compare(Map.Entry<String, AtomicInteger> o1, Map.Entry<String, AtomicInteger> o2) {
                return o2.getValue().get() - o1.getValue().get(); // 降序排序
            }
        }).collect(Collectors.toList());
    }

    @Override
    public int destory() {
        return 0;
    }
}
