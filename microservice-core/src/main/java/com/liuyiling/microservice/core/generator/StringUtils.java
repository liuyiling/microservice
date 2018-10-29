package com.liuyiling.microservice.core.generator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author liuyiling
 */
public class StringUtils {

    public static final char UNDERLINE = '_';
    public static final Pattern PATTERN = Pattern.compile("_");

    /**
     * e.g. orgTerminal-->OrgTerminal
     * @param orgStr
     * @return
     */
    public static String firstChar2UpperCase(String orgStr) {
        StringBuffer sb = new StringBuffer(orgStr);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        orgStr = sb.toString();
        return orgStr;
    }

    /**
     * e.g. orgTerminal-->org_terminal
     * @param param
     * @return
     */
    public static String camel2Underline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * e.g. org_terminal-->orgTerminal
     * @param param
     * @return
     */
    public static String underLine2Camel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        StringBuilder sb = new StringBuilder(param);
        Matcher mc = PATTERN.matcher(param);
        int i = 0;
        while (mc.find()) {
            int position = mc.end() - (i++);
            sb.replace(position - 1, position + 1, sb.substring(position, position + 1).toUpperCase());
        }
        return sb.toString();
    }
}
