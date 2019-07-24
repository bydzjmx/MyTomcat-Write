package com.jmx.servlet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * servlet映射类,封装自定义servlet的信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServletMapping {
    //servlet别名
    private String servletName;
    //servlet的对应url
    private String url;
    //servlet的类
    private String clazz;
}
