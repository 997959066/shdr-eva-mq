package com.shdr.eva.mq.common;

import lombok.Getter;
import lombok.Setter;

/**
 * 测试对象
 */
@Getter
@Setter
public class User {

    private Integer id;
    private String name;
    private Integer age;

    public User(Integer id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
