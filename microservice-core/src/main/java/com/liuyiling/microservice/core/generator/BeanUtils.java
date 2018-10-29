package com.liuyiling.microservice.core.generator;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * velocity需要用的对象描述工具
 *
 * @author liuyiling
 */
public class BeanUtils {

    public static Map<String, Object> getValueMap(Object obj) {
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            boolean accessFlag = fields[i].isAccessible();
            try {
                fields[i].setAccessible(true);
                Object o = fields[i].get(obj);
                if (o != null) {
                    map.put(varName, o);
                }
            } catch (Exception e) {
            } finally {
                fields[i].setAccessible(accessFlag);
            }
        }
        return map;
    }
}
