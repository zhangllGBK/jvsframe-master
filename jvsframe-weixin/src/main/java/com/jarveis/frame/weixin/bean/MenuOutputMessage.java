package com.jarveis.frame.weixin.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Service_Center_messages.html#">...</a>客服接口-发消息
 * </pre>
 *
 * @author liuguojun
 * @since 2021-10-20
 */
public class MenuOutputMessage extends OutputMessage {

    private List<Menu> menus;
    private String headContent;
    private String tailContent;

    public MenuOutputMessage() {
        setMsgType("msgmenu");
        this.menus = new ArrayList<Menu>();
    }

    public void addArticle(Menu menu) {
        this.menus.add(menu);
    }

    public String getHeadContent() {
        return headContent;
    }

    public void setHeadContent(String headContent) {
        this.headContent = headContent;
    }

    public String getTailContent() {
        return tailContent;
    }

    public void setTailContent(String tailContent) {
        this.tailContent = tailContent;
    }

    public String toWeixin() {
        String str = "";
        return str;
    }

    public String toCustom() {
        String str = "\"touser\": \"" + getToUserName() + "\"," +
                "  \"msgtype\": \"msgmenu\"," +
                "  \"msgmenu\": {" +
                "    \"head_content\": \"" + getHeadContent() + " \"," +
                "    \"list\": [";
        for (Menu menu : menus) {
            str += "      {" +
                    "        \"id\": \"" + menu.getId() + "\"," +
                    "        \"content\": \"" + menu.getContent() + "\"" +
                    "      },";
        }
        if (!menus.isEmpty()) {
            str = str.substring(0, str.length() - 1);
        }
        str += "    ]," +
                "    \"tail_content\": \"" + getTailContent() + "\"" +
                "  }";
        return str;
    }

    class Menu {
        private String id;
        private String content;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
