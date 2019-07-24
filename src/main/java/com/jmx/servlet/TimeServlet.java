package com.jmx.servlet;

import com.jmx.http.MyRequest;
import com.jmx.http.MyResponse;

import java.io.IOException;

/**
 * 测试使用，访问localhost:port/time
 */
public class TimeServlet extends MyServlet{
    public void doGet(MyRequest myRequest, MyResponse myResponse) {
        try{
            myResponse.write("GET TIME");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doPost(MyRequest myRequest, MyResponse myResponse) {
        try{
            myResponse.write("SERVICE TIME");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
