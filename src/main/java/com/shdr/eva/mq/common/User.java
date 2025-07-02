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

    public User(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
