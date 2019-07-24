package com.jmx.http;

import lombok.Data;

import java.io.IOException;
import java.io.InputStream;

/**
 * Request实现，封装请求对象的信息
 * 通过输入流，对HTTP协议进行解析，拿到HTTP请求头的方法以及URL。
 */
@Data
public class MyRequest {
    //请求的url
    private String url;
    //请求的方法
    private String method;

    /**
     * 传入流，封装请求对象
     * @param inputStream
     * @throws IOException
     */
    public MyRequest(InputStream inputStream) throws IOException{
        String httpRequest = "";
        byte[] httpRequestBytes =  new byte[1024];
        int length = 0;
        //从输入流中读取一些字节数并将它们存储到缓存数组中，返回数组的长度
        if((length = inputStream.read(httpRequestBytes))>0){
            httpRequest = new String(httpRequestBytes,0,length);
        }
        //解析http请求协议，获取url和method
        String httpHead = httpRequest.split("\n")[0];
        //以空白符作为拆分基准，获取url和method
        url = httpHead.split("\\s")[1];
        method = httpHead.split("\\s")[0];
    }
}
