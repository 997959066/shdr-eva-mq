package com.shdr.eva.mq.serializer;

import com.alibaba.fastjson2.TypeReference;

public interface ValueSerializer<S> {

    <T> T unSerialize(S value);

    <T> T unSerialize(String value, Class<T> clz);

    <T> T unSerialize(String value, TypeReference<T> clz);

    <T> S serialize(T value);
}
