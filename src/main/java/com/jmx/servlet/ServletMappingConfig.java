package com.jmx.servlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Servlet配置
 */
public class ServletMappingConfig {
    public static List<ServletMapping> servletMappingList = new ArrayList<>();
    static {
        servletMappingList.add(new ServletMapping("HelloWorld","/","com.jmx.servlet.HelloWorldServlet"));
        servletMappingList.add(new ServletMapping("Time","/time","com.jmx.servlet.TimeServlet"));
    }
}
