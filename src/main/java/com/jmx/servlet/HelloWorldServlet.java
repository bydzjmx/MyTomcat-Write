package com.jmx.servlet;

import com.jmx.http.MyRequest;
import com.jmx.http.MyResponse;

import java.io.IOException;

/**
 * 测试使用,访问localhost:port/
 */
public class HelloWorldServlet extends MyServlet{

    public void doGet(MyRequest myRequest, MyResponse myResponse) {
        try{
            myResponse.write("Welcome to myTomcat!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doPost(MyRequest myRequest, MyResponse myResponse) {
        try{
            myResponse.write("Hello service world");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
