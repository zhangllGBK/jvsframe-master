package com.jarveis.frame.util;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 请求对象
 *
 * @author liuguojun
 */
public class Param {

    private static final Logger log = LoggerFactory.getLogger(Param.class);

    // 请求
    public static final String REQ = "Req";
    // 响应
    public static final String RESP = "Resp";
    // 请求头部
    public static final String HEAD = "head";
    // 请求内容
    public static final String BODY = "body";

    public static final String LABEL_FUNCID = "@funcId"; // 方法标识标签
    public static final String LABEL_METHOD = "@method"; // 数据类型
    public static final String LABEL_DATATYPE = "@dataType"; // 数据类型
    public static final String LABEL_DEVICE = "@device"; // 客户端
    public static final String LABEL_TOKEN = "@token"; // 访问令牌
    public static final String LABEL_ERROR = "@errcode"; // 错误标签
    public static final String LABEL_ERROR_DESC = "@errdesc"; // 错误错误
    public static final String LABEL_EXTEND = "@extend"; // 扩展字段
    public static final String LABEL_REMOTEIP = "@remoteIp"; // 客户端ip
    public static final String LABEL_PARENTID = "@parentId"; // 父请求流水号
    public static final String LABEL_REQUESTID = "@requestId"; // 请求流水号
    public static final String LABEL_USEDTIME = "@usedTime"; // 请求耗时
    public static final String LABEL_ENCRYPTMODE = "@encodeMode"; // 加密模式
    public static final String LABEL_ENCRYPTKEY = "@encodeKey"; // 加密密钥

    public static final String DT_XML = "xml"; // 数据类型
    public static final String DT_HTML = "html"; // 数据类型
    public static final String DT_JSON = "json"; // 数据类型

    public static final String PROTOCOL_BASE64 = "BASE64:\\\\";

    public static final String ERROR_SUCCESS = "0000"; // 操作成功
    public static final int ERROR_EXCEPTION = 9999; // 操作成功

    public static final char PERFIX_LABEL = '@'; // label前缀
    public static final char PERFIX_XML = '<'; // xml前缀
    public static final char PERFIX_JSON = '{'; // json前缀

    private Element element;
    private String tagName;

    public Param(String str) throws ParamException {
        if (StringUtils.isBlank(str)) {
            throw new ParamException("构建数据包异常");
        }

        if (REQ.equals(str) || RESP.equals(str)) {
            element = new DefaultElement(str);
            addParam(HEAD);
            addParam(BODY);
        } else {
            char prefix = str.charAt(0);
            if (prefix == PERFIX_XML) {
                parseDomStr(str);
            } else if (prefix == PERFIX_JSON) {
                parseJsonStr(str);
            }  else {
                throw new ParamException("( " + str + " )构建数据包异常");
            }
        }
        tagName = element.getName();



        checkBuilder();
    }

    /**
     * 构造器
     *
     * @param ele 节点
     */
    public Param(Element ele) {
        this.element = ele;
        this.tagName = this.element.getName();
    }

    /**
     * AES/CBC/PKCS5Padding 加密
     *
     * @param str 明文
     * @param key 密钥
     * @return 密文
     */
    private String encryptAES1(String str, String key) {
        try {
            byte[] byteContent = str.getBytes(StandardCharsets.UTF_8);
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(keyBytes);
            // 指定加密的算法、工作模式和填充方式
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(byteContent);
            // 同样对加密后数据进行 base64 编码
            return Base64.getUrlEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("AES encrypt Exception,content = " + str, e);
        }

        return StringUtils.EMPTY;
    }

    /**
     * AES/CBC/PKCS5Padding 解密
     *
     * @param str 密文
     * @param key 密钥
     * @return 明文
     */
    private String decryptAES1(String str, String key) {
        try {
            byte[] encryptedBytes = Base64.getUrlDecoder().decode(str);
            byte[] keyBytes = key.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(keyBytes);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] result = cipher.doFinal(encryptedBytes);

            return new String(result, "UTF-8");
        } catch (Exception e) {
            log.error("AES decrypt Exception,content = " + str, e);
        }

        return StringUtils.EMPTY;
    }

    /**
     * 解析xml字符串
     *
     * @param str xml字符串
     */
    private void parseDomStr(String str) {
        try {
            Document doc = DocumentHelper.parseText(str);
            element = doc.getRootElement();
        } catch (Exception ex) {
            element = new DefaultElement(str);
            log.error("domstr=" + str);
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 解析json字符串
     *
     * @param str json格式的字符串
     */
    private void parseJsonStr(String str) {
        try {
            Map map = (Map) JsonUtil.parse(str);

            element = new DefaultElement(REQ);
            this.setPropertys(map);
        } catch (Exception ex) {
            element = new DefaultElement(str);
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 校验构建数据
     */
    private void checkBuilder() {
        Map props = getPropertys();
        removePropertys();

        Param head = getHead();
        if (head == null) {
            head = addParam(HEAD);
        }
        Param body = getBody();
        if (body == null) {
            body = addParam(BODY);
        }
        List<Param> childs = getChilds(null);
        for (Param child : childs) {
            String tagName = child.getTagName();
            if ((!HEAD.equals(tagName)) && (!BODY.equals(tagName))) {
                removeChilds(tagName);

                body.addParam(child.getElement(false));
            }
        }
        body.setPropertys(props);
    }

    /**
     * 是否成功
     *
     * @return
     */
    public boolean isSuccess(){
        int errcode = getHead().getInteger(LABEL_ERROR, NumberUtils.INTEGER_MINUS_ONE);
        return NumberUtils.toInt(ERROR_SUCCESS) == errcode;
    }

    /**
     * 是否异常
     *
     * @return
     */
    public boolean isException(){
        int errcode = getHead().getInteger(LABEL_ERROR, ERROR_EXCEPTION);
        return ERROR_EXCEPTION == errcode;
    }

    /**
     * 设置属性值
     *
     * @param path
     * @param value
     */
    public void setProperty(String path, Object value) {
        if (value != null) {
            if ("@_text".equals(path)) {
                element.addCDATA((String) value);
            } else if (value instanceof String) {
                setProperty(path, (String) value);
            } else if (value instanceof Short) {
                setProperty(path, ((Short) value).shortValue());
            } else if (value instanceof Integer) {
                setProperty(path, ((Integer) value).intValue());
            } else if (value instanceof Long) {
                setProperty(path, ((Long) value).longValue());
            } else if (value instanceof Float) {
                setProperty(path, ((Float) value).floatValue());
            } else if (value instanceof Double) {
                setProperty(path, ((Double) value).doubleValue());
            } else if (value instanceof Boolean) {
                setProperty(path, ((Boolean) value).booleanValue());
            } else if (value instanceof BigDecimal) {
                setProperty(path, (BigDecimal) value);
            } else if (value instanceof Date) {
                setProperty(path, (Date) value);
            } else if (value instanceof String[]) {
                setProperty(path, (String[]) value);
            } else if (value instanceof Map) {
                Param param = this.addParam(path.substring(1));
                param.setPropertys((Map) value);
            } else if (value instanceof List) {
                List list = (List) value;
                for (Object obj : list) {
                    if (obj instanceof Map) {
                        Param param = this.addParam(path.substring(1));
                        param.setPropertys((Map) obj);
                    } else {
                        setProperty(path, ((List)value).toArray());
                        break;
                    }
                }

            }
        }
    }

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    public void setProperty(String path, boolean value) {
        setProperty(path, String.valueOf(value));
    }

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param date 属性值
     */
    public void setProperty(String path, Date date) {
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            setProperty(path, format.format(date));
        }
    }

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param decimal 属性值
     */
    public void setProperty(String path, BigDecimal decimal) {
        if (decimal != null) {
            setProperty(path, decimal.toString());
        }
    }

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    public void setProperty(String path, float value) {
        setProperty(path, String.valueOf(value));
    }

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    public void setProperty(String path, double value) {
        setProperty(path, String.valueOf(value));
    }

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    public void setProperty(String path, int value) {
        setProperty(path, String.valueOf(value));
    }

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    public void setProperty(String path, long value) {
        setProperty(path, String.valueOf(value));
    }

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    public void setProperty(String path, short value) {
        setProperty(path, String.valueOf(value));
    }

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    public void setProperty(String path, Object[] value) {
        if (value != null) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < value.length; i++) {
                buf.append(value[i]);
                if (i < value.length - 1) {
                    buf.append(CharacterUtil.SEPARATOR);
                }
            }
            setProperty(path, buf.toString());
        }
    }

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    public void setProperty(String path, String value) {
        if (StringUtils.isNotEmpty(path)) {
            path = path.trim();
            if (path.charAt(0) == PERFIX_LABEL) {
                path = path.substring(1);
                if (value == null) {
                    value = StringUtils.EMPTY;
                }
                Attribute attr = element.attribute(path);
                if (attr != null) {
                    attr.setValue(value);
                } else {
                    element.addAttribute(path, value);
                }
            }
        }
    }

    /**
     * 设置属性集
     *
     * @param map 属性集合
     */
    public void setPropertys(Map map) {
        Set<Entry<String, Object>> set = map.entrySet();

        Iterator<Entry<String, Object>> it = set.iterator();
        while (it.hasNext()) {
            Entry<String, Object> entry = it.next();
            String path = entry.getKey();
            if (path.charAt(0) != PERFIX_LABEL) {
                path = PERFIX_LABEL + path;
            }
            Object value = entry.getValue();
            setProperty(path, value);
        }
    }

    /**
     * 删除属性
     *
     * @param path 属性名
     */
    public void removeProperty(String path) {
        if (path != null && path.charAt(0) == PERFIX_LABEL) {
            path = path.substring(1);
            Attribute attr = element.attribute(path);
            if (attr != null) {
                element.remove(attr);
            }
        }
    }

    /**
     * 删除当前节点所有属性
     */
    public void removePropertys() {
        Map props = this.getPropertys();
        Set keys = props.keySet();
        for (Object key : keys) {
            String path = PERFIX_LABEL + (String) key;
            this.removeProperty(path);
        }

    }

    /**
     * 添加CDATA
     *
     * @param content 内容
     */
    public void addCDATA(String content) {
        if (content != null && !"".equals(content)) {
            element.addCDATA(PROTOCOL_BASE64 + Base64.getUrlEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8)));
        }
    }

    /**
     * 添加子节点
     *
     * @param path
     * @return
     */
    public Param addParam(String path) {
        Param vo = null;

        if (path != null && !"".equals(path)) {
            Element ele = element.addElement(path);
            vo = new Param(ele);
        }

        return vo;
    }

    /**
     * 添加子节点
     *
     * @param ele
     * @return Param
     */
    public Param addParam(Element ele) {
        Param vo = null;

        if (ele != null) {
            element.add(ele);
            vo = new Param(ele);
        }

        return vo;
    }

    /**
     * 获取属性值
     *
     * @param path
     * @return
     */
    public BigDecimal getBigDecimal(String path) {
        return getBigDecimal(path, "0");
    }

    /**
     * 获取属性值, 如果不存在, 则返回默认值
     *
     * @param path 属性
     * @param defaultValue 默认值
     * @return
     */
    public BigDecimal getBigDecimal(String path, String defaultValue) {
        return new BigDecimal(getString(path, defaultValue));
    }

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return
     */
    public Date getDate(String path) {
        return this.getDate(path, DateUtil.getDate());
    }

    /**
     * 获取属性值, 如果不存在, 则返回默认值
     *
     * @param path 属性
     * @param defaultValue 默认值
     * @return
     */
    public Date getDate(String path, Date defaultValue) {
        Date date = null;
        String value = getProperty(path);
        if (StringUtils.isEmpty(value)) {
            date = defaultValue;
        } else {
            date = DateUtil.getDate(value);
        }

        return date;
    }

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return
     */
    public boolean getBoolean(String path) {
        return this.getBoolean(path, false);
    }

    /**
     * 获取属性值, 如果不存在, 则返回默认值
     *
     * @param path         属性名
     * @param defaultValue 默认值
     * @return
     */
    public boolean getBoolean(String path, boolean defaultValue) {
        String value = getString(path);
        if (StringUtils.isNotEmpty(value)) {
            return BooleanUtils.toBoolean(value);
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return
     */
    public double getDouble(String path) {
        return getDouble(path, NumberUtils.DOUBLE_ZERO);
    }

    /**
     * 获取属性值, 如果不存在, 则返回默认值
     *
     * @param path 属性
     * @param defaultValue 默认值
     * @return 属性值
     */
    public double getDouble(String path, double defaultValue) {
        return NumberUtils.toDouble(getString(path), defaultValue);
    }

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return 属性值
     */
    public short getShort(String path) {
        return getShort(path, NumberUtils.SHORT_ZERO);
    }

    /**
     * 获取属性值, 如果不存在, 则返回默认值
     *
     * @param path 属性
     * @param defaultValue 默认值
     * @return
     */
    public short getShort(String path, short defaultValue) {
        return NumberUtils.toShort(getString(path), defaultValue);
    }

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return
     */
    public int getInteger(String path) {
        return getInteger(path, NumberUtils.INTEGER_ZERO);
    }

    /**
     * 获取属性值, 如果不存在, 则返回默认值
     *
     * @param path 属性
     * @param defaultValue 默认值
     * @return
     */
    public int getInteger(String path, int defaultValue) {
        return NumberUtils.toInt(getString(path), defaultValue);
    }

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return
     */
    public long getLong(String path) {
        return getLong(path, NumberUtils.LONG_ZERO);
    }

    /**
     * 获取属性值, 如果不存在, 则返回默认值
     *
     * @param path 属性
     * @param defaultValue 默认值
     * @return
     */
    public long getLong(String path, long defaultValue) {
        return NumberUtils.toLong(getString(path), defaultValue);
    }

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return
     */
    public String getString(String path) {
        return getString(path, StringUtils.EMPTY);
    }

    /**
     * 获取属性值， 如果不存在返回默认值
     *
     * @param path 属性
     * @return
     */
    public String getString(String path, String defvalue) {
        String value = getProperty(path);
        if (StringUtils.isEmpty(value)) {
            value = defvalue;
        }

        return value;
    }

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return
     */
    private String getProperty(String path) {
        String value = null;
        if (StringUtils.isNotEmpty(path)) {
            path = path.trim();
            if (path.charAt(0) == PERFIX_LABEL) {
                path = path.substring(1);
                value = element.attributeValue(path);
            }
        }
        return value;
    }

    /**
     * 获取属性信息
     *
     * @return 属性集合
     */
    public Map<String, String> getPropertys() {
        Map<String, String> map = null;

        List<Attribute> attrs = element.attributes();
        map = new HashMap<String, String>(attrs.size());
        for (int i = 0; i < attrs.size(); i++) {
            Attribute attr = attrs.get(i);
            map.put(attr.getName(), attr.getValue());
        }

        return map;
    }

    /**
     * 获取CDATA值
     *
     * @return
     */
    public String getCDATA() {
        String text = element.getTextTrim();

        if (text.startsWith(PROTOCOL_BASE64)) {
            return new String(Base64.getUrlDecoder().decode(text.substring(9)), StandardCharsets.UTF_8);
        }

        return text;
    }

    /**
     * 获取数据包头部
     *
     * @return
     */
    public Param getHead() {
        return getChild(HEAD);
    }

    /**
     * 获取数据包主体
     *
     * @return
     */
    public Param getBody() {
        return getChild(BODY);
    }

    /**
     * 通过节点名称删除单个节点
     *
     * @param path 节点名称
     */
    public void removeChild(String path) {
        Element temp = null;

        if (StringUtils.isNotEmpty(path)) {
            temp = element.element(path);
        } else {
            List eleList = element.elements();
            if (!eleList.isEmpty()) {
                temp = (Element) eleList.get(0);
            }
        }

        if (temp != null) {
            element.remove(temp);
        }

    }

    /**
     * 通过节点名称删除多个节点
     *
     * @param path 节点名称
     */
    public void removeChilds(String path) {
        List eleList = null;

        if (StringUtils.isNotEmpty(path)) {
            eleList = element.elements(path);
        } else {
            eleList = element.elements();
        }

        if (eleList != null && !eleList.isEmpty()) {
            for (int i = 0; i < eleList.size(); i++) {
                Element ele = (Element) eleList.get(i);
                element.remove(ele);
            }
        }
    }

    /**
     * 获取子节点
     *
     * @param path 节点名称
     * @return
     */
    public Param getChild(String path) {
        Param vo = null;

        if (StringUtils.isNotEmpty(path)) {
            Element temp = element.element(path);
            if (temp != null) {
                vo = new Param(temp);
            }
        } else {
            List list = element.elements();
            if (!list.isEmpty()) {
                Element temp = (Element) list.get(0);
                vo = new Param(temp);
            }
        }

        return vo;
    }

    /**
     * 获取子节点列表
     *
     * @param path 节点名称
     * @return
     */
    public List<Param> getChilds(String path) {
        List<Param> voList = null;

        List eleList = null;
        if (StringUtils.isNotEmpty(path)) {
            eleList = element.elements(path);
        } else {
            eleList = element.elements();
        }
        voList = new ArrayList<Param>(eleList.size());

        for (int i = 0; i < eleList.size(); i++) {
            Element ele = (Element) eleList.get(i);
            voList.add(new Param(ele));
        }

        return voList;
    }

    /**
     * 获取标签名称
     *
     * @return String 当前节点的标签名称
     */
    public String getTagName() {
        return this.tagName;
    }

    /**
     * 返回根节点
     *
     * @return
     */
    public Element getElement(boolean is) {
        Element ele = element;

        if (is) {
            while (ele.getParent() != null) {
                ele = ele.getParent();
            }
        } else {
            ele.setParent(null);
        }

        return ele;
    }

    /**
     * 转换成Map对象
     *
     * @return 当前节点的Map对象
     */
    private Map toMap(Element e) {
        Map map = new HashMap();

        String text = e.getText();
        if (!StringUtils.isBlank(text)) {
            e.addAttribute("_text", StringUtils.trim(text));
        }

        List attrs = e.attributes();
        for (int i = 0; i < attrs.size(); i++) {
            Attribute attr = (Attribute) attrs.get(i);
            map.put(attr.getName(), attr.getValue());
        }

        List eles = e.elements();
        for (int i = 0; i < eles.size(); i++) {
            Element ele = (Element) eles.get(i);
            String key = ele.getName();
            Map value = toMap(ele);
            Object obj = map.get(key);
            if (obj != null) {
                if (obj instanceof Map) {
                    List list = new ArrayList();
                    list.add(obj);
                    list.add(value);
                    map.put(key, list);
                } else if (obj instanceof List) {
                    List list = (List) obj;
                    list.add(value);
                    map.put(key, list);
                }
            } else {
                map.put(key, value);
            }
        }

        return map;
    }

    /**
     * 序列化为json格式的字符串
     *
     * @return
     */
    public String toJsonString() {
        String str =  JsonUtil.toJson(toMap(this.element));

        return str;
    }

    /**
     * 序列化为xml格式的字符串
     *
     * @return
     */
    public String toXmlString() {
        Element root = getElement(false);
        Document document = root.getDocument();
        if (document == null) {
            document = DocumentHelper.createDocument();
            document.setRootElement(root);
        }
        String str =  document.asXML().replaceAll("\\n", "");

        return str;
    }

}
