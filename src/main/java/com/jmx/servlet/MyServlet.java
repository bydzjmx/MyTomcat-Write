package com.jmx.servlet;

import com.jmx.http.MyRequest;
import com.jmx.http.MyResponse;

/**
 * Tomcat是满足servlet规范的容器，提供servlet需求的API，如doGet，doPost，service等
 */
public abstract class MyServlet {
    public abstract void doGet(MyRequest myRequest, MyResponse myResponse);
    public abstract void doPost(MyRequest myRequest, MyResponse myResponse);
    public void service(MyRequest myRequest,MyResponse myResponse){
        if(myRequest.getMethod().equalsIgnoreCase("POST")){
            doPost(myRequest,myResponse);
        }else if (myRequest.getMethod().equalsIgnoreCase("GET")){
            doGet(myRequest,myResponse);
        }
    }
}
