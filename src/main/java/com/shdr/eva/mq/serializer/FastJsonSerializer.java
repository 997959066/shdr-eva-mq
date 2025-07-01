package com.shdr.eva.mq.serializer;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson2.*;
import com.disney.eva.framework.utils.type.ArrayUtils;
import com.disney.eva.framework.utils.type.ClassUtils;
import com.disney.eva.framework.utils.type.StringUtils;

public class FastJsonSerializer extends AbstractStringSerializer {

    static {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    private static final Class<?>[] PARSE_CLASSES = {
            boolean[].class,
            char[].class,
            byte[].class,
            short[].class,
            int[].class,
            long[].class,
            float[].class,
            double[].class
    };

    @Override
    public <T> T unSerialize(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        Object v = JSON.parse(value, JSONReader.Feature.SupportAutoType);
        if (v == null) {
            return null;
        }
        return (T) v;
    }

    @Override
    public <T> T unSerialize(String value, Class<T> clazz) {
        if (StringUtils.isBlank(value)) {
            return ClassUtils.convert(null, clazz);
        }
        if (String.class.equals(clazz)) {
            try {
                return JSON.parseObject(value, clazz);
            } catch (JSONException e) {
                return (T) value;
            }
        } else if (ArrayUtils.in(clazz, PARSE_CLASSES)) {
            return JSON.parseObject(value, clazz);
        }

        Object obj = JSON.parse(value);
        if (obj instanceof JSONObject) {
            JSONObject json = (JSONObject) obj;
            final String typeKey = com.alibaba.fastjson.JSON.DEFAULT_TYPE_KEY;
            String className = json.getString(typeKey);
            if (clazz != null && !clazz.getName().equals(className)) {
                json.remove(typeKey);
            }
            return JSON.toJavaObject(json, clazz);
        } else if (obj instanceof JSON) {
            return JSON.toJavaObject((JSON) obj, clazz);
        } else {
            return ClassUtils.convert(obj, clazz);
        }
    }

    @Override
    public <T> T unSerialize(String value, TypeReference<T> type) {
        return JSONObject.parseObject(value, type, JSONReader.Feature.SupportAutoType);
    }

    @Override
    public <T> String serialize(T value) {
        if (value == null) {
            return null;
        }
        return JSON.toJSONString(value, JSONWriter.Feature.WriteClassName);
    }

}
