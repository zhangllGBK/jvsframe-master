package com.jarveis.frame.dbs.service;

import com.jarveis.frame.dbs.*;
import com.jarveis.frame.dbs.ant.Function;
import com.jarveis.frame.dbs.ant.Scope;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Service控制台
 *
 * @author liuguojun
 */
@Function(code = ServiceCode.DBS_DASH_BOARD_SERVICE)
public class DashBoardService implements Service {

    private static final Logger log = LoggerFactory.getLogger(DashBoardService.class);

    private final String TABLE = "<table width=\"100%\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"200\">服务编号</td><td width=\"200\">服务类型</td><td width=\"100\">访问权限</td><td width=\"150\">前置拦截</td><td width=\"150\">后置拦截</td><td>服务节点</td></tr>{tbody}</table>";
    private final String TD = "<td{rowspan}>{content}</td>";
    private final String ROWSPAN = " rowspan=\"{length}\"";
    private final String USER_VIEW = "<a href=\"{service}.service?serviceCode={serviceCode}\">{serviceCode}</a>".replace("{service}", ServiceCode.SLUICE_DASH_BOARD_SERVICE);
    private final String SYS_VIEW = "{serviceCode}";

    public Param callService(Param in) {
        Param out = null;

        try {
            if (log.isDebugEnabled()) {
                log.debug(in.toXmlString());
            }
            out = new Param(Param.RESP);

            // 获取serviceServer配置
            String serviceServer = DbsCache.getConst(DbsConst.DBS_SERVICE_SERVER);
            if (StringUtils.isEmpty(serviceServer)) {
                StringBuilder rowsBuf = new StringBuilder();
                StringBuilder rowBuf = new StringBuilder();
                // 当前节点的所有服务（包含已同步的其它节点服务）
                List<String> serviceKeyList = DbsCache.listServiceKey();
                Collections.sort(serviceKeyList);
                // 构建同步到master的服务
                for (String sk : serviceKeyList) {
                    ServiceWrapper wrapper = DbsCache.getService(sk);
                    if (wrapper == null) {
                        // 如果不是本地服务，则不需要返回
                        continue;
                    }
                    Param serviceParam = out.getBody().addParam("service");
                    serviceParam.setProperty("@node", wrapper.format());
                    serviceParam.setProperty("@func", sk);

                    String[] nodes = StringUtils.split(wrapper.format(), ",");
                    for (int i = 0; i < nodes.length; i++) {
                        rowBuf.append("<tr>");
                        if (i == 0) {
                            if (NumberUtils.toInt(sk, NumberUtils.INTEGER_ZERO) < 10000) {
                                rowBuf.append(TD.replace("{content}", SYS_VIEW.replace("{serviceCode}", sk)));
                            } else {
                                rowBuf.append(TD.replace("{content}", USER_VIEW.replace("{serviceCode}", sk)));
                            }
                            rowBuf.append(TD.replace("{content}", NumberUtils.toInt(sk, NumberUtils.INTEGER_ZERO) < 10000 ? "系统服务" : "用户服务"));
                            rowBuf.append(TD.replace("{content}", wrapper.getScope() == Scope.PRIVATE ? "私有" : "公开"));
                            rowBuf.append(TD.replace("{content}", StringUtils.join(wrapper.getBefores(), ",")));
                            rowBuf.append(TD.replace("{content}", StringUtils.join(wrapper.getAfters(), ",")));
                        }
                        rowBuf.append(TD.replace("{content}", nodes[i]).replace("{rowspan}", StringUtils.EMPTY));
                        rowBuf.append("</tr>");
                    }
                    rowsBuf.append(rowBuf.toString().replace("{rowspan}", nodes.length > 1 ? ROWSPAN.replace("{length}", String.valueOf(nodes.length)) : StringUtils.EMPTY));
                    rowBuf.delete(0, rowBuf.length());
                }
                out.getBody().addCDATA(TABLE.replace("{tbody}", rowsBuf.toString()));
            }
            out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_SUCCESS);
        } catch (Exception ex) {
            if (out != null) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
            }
            log.error(ex.getMessage(), ex);
        }

        return out;
    }

}
