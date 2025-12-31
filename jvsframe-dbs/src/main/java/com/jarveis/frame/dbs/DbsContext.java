package com.jarveis.frame.dbs;

import com.jarveis.frame.bean.ReflectionUtils;
import com.jarveis.frame.config.ClassScanner;
import com.jarveis.frame.dbs.ant.After;
import com.jarveis.frame.dbs.ant.Before;
import com.jarveis.frame.dbs.ant.Function;
import com.jarveis.frame.dbs.ant.Interceptor;
import com.jarveis.frame.dbs.filter.Filter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * 上下文
 *
 * @author poyexinghun
 * @since 2018-03-18
 */
public final class DbsContext {

    private static final Logger log = LoggerFactory.getLogger(DbsContext.class);

    public DbsContext() {
    }

    /**
     * 初始化Dbs
     */
    public static void init() {
        Set<String> classSet = ClassScanner.getClassSet();

        // 遍历扫描出的类，
        for (String clazz : classSet) {
            buildFilter(clazz);
            buildFunction(clazz);
        }
    }

    /**
     * 构建服务
     *
     * @param clazz class文件
     */
    private static void buildFunction(String clazz) {
        try {
            Class<?> objClass = Class.forName(clazz);
            Function function = objClass.getAnnotation(Function.class);
            if (function != null) {
                String beforeFilters = null, afterFilters = null;
                Object service = ReflectionUtils.newInstance(clazz);
                if (service == null) {
                    log.error(clazz + "未能实例化！");
                    return ;
                }
                if (!(service instanceof Service)) {
                    log.error(clazz + "未实现 com.jarveis.dbs.core.Service接口");
                    return ;
                }
                // 校验function code是否为空
                String functionCode = function.code();
                if (StringUtils.isEmpty(functionCode)) {
                    return ;
                }
                // 追加前置拦截
                Before before = objClass.getAnnotation(Before.class);
                if (before != null) {
                    beforeFilters = before.filters();
                }
                // 追加后置拦截
                After after = objClass.getAnnotation(After.class);
                if (after != null) {
                    afterFilters = after.filters();
                }

                String nodeType = StringUtils.defaultIfEmpty(DbsCache.getConst(DbsConst.DBS_NODE), DbsConst.DBS_NODE_SERVICE);
                if (DbsConst.DBS_NODE_SERVICE.equals(nodeType)) {
                    if (DbsUtils.isFuncId(functionCode) > 0) {
                        //　服务包装
                        ServiceWrapper wrapper = new ServiceWrapper(functionCode, beforeFilters, afterFilters);
                        wrapper.setTransaction(function.transaction());
                        wrapper.setScope(function.scope());
                        wrapper.add((Service) service);
                        DbsCache.putLocalService(functionCode, wrapper);
                    }
                } else {
                    if (DbsUtils.isFuncId(functionCode) == 0) {
                        //　服务包装
                        ServiceWrapper wrapper = new ServiceWrapper(functionCode, beforeFilters, afterFilters);
                        wrapper.setTransaction(function.transaction());
                        wrapper.setScope(function.scope());
                        wrapper.add((Service) service);
                        DbsCache.putLocalService(functionCode, wrapper);
                    }
                }

            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 构建过滤器
     *
     * @param clazz class文件
     */
    private static void buildFilter(String clazz) {
        try {
            Class<?> objClass = Class.forName(clazz);
            Interceptor interceptor = objClass.getAnnotation(Interceptor.class);
            if (interceptor != null) {
                Object filter = ReflectionUtils.newInstance(clazz);
                if (filter == null) {
                    log.error(clazz + "未能实例化！");
                    return ;
                }
                if (!(filter instanceof Filter)) {
                    log.error(clazz + "未实现 com.jarveis.frame.dbs.filter.Filter接口");
                    return ;
                }

                // 添加到缓存
                FilterWrapper fw = new FilterWrapper(interceptor.code(), (Filter) filter);
                DbsCache.putFilter(fw.getCode(), fw);

                // 初始化过滤器
                fw.get().init();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}
