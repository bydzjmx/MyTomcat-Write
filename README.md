# 手写框架汇总
## 手写简易Tomcat
### 1. Tomcat框架介绍
Tomcat 是由 Apache 开发的一个 Servlet 容器，实现了对 Servlet 和 JSP 的支持，并提供了作为Web服务器的一些特有功能，是一个运行JAVA的网络服务器，底层是Socket的一个程序。

### 2. Tomcat的简单工作原理：
Tomcat作为web服务器，接收来自客户端的请求，处理并封装成内部的request和response对象，根据url交给对应的servlet进行处理，然后将response返回给客户端。
 ![image](http://assets.processon.com/chart_image/5d37e451e4b092b33466d6e7.png)

### 3. 手写Tomcat设计思路：
1. 定义MyRequest和MyReSponse，对请求和响应进行处理和封装
2. 定义MyServlet，具有相应的service/doGet/doPost方法
3. 定义ServletMapping，将url和servlet进行关联
4. 定义MyTomcat，建立Socket连接，封装request对象和response对象；利用反射，根据url找到对应的servlet进行处理；最后进行分发输出

### 4. 实现自己的Tomcat
1.定义MyRequest和MyReSponse
```java
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
```

```java
/**
 * 封装响应对象
 * 基于HTTP协议的格式进行输出写入。
 */
@Data
public class MyResponse {
    private OutputStream outputStream;

    public MyResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(String content) throws IOException{
        //构建StringBuffer输出
        StringBuffer httpResponse = new StringBuffer();
        httpResponse.append("HTTP/1.1 200 OK\n")
                .append("Content-Type: text/html\n")
                .append("\r\n")
                .append("<html><body>")
                .append(content)
                .append("<body><html>");
        //输出
        outputStream.write(httpResponse.toString().getBytes());
        //关闭
        outputStream.close();
    }
}
```

2. 定义MyServlet
```java
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
```

3. 定义ServletMapping

```java
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
```
```java
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
```

4. 定义MyTomcat

```java
/**
 * 自定义Tomcat类
 * Tomcat的处理流程：
 * 把URL对应处理的Servlet关系形成，解析HTTP协议，封装请求/响应对象，利用反射实例化具体的Servlet进行处理即可。
 */
public class MyTomcat {
    private int port = 8080;
    private Map<String,String> urlServletMap = new HashMap<>();

    public MyTomcat(int port) {
        this.port = port;
    }
    //启动myTomcat
    public static void main(String[] args) {
        MyTomcat myTomcat = new MyTomcat(8080);
        myTomcat.start();
    }
    /**
     * Tomcat启动类
     * 1. 建立连接，封装request对象和response对象
     * 2. 利用反射，根据url找到对应的servlet进行处理
     * 3. 进行分发输出
     */
    public void start(){
        //初始化 url和servlet的对应关系，urlServletMap中
        initServletMapping();
        //建立ServerSocket通信，此类实现服务器套接字。服务器套接字等待请求通过网络进入。
        // 它根据该请求执行一些操作，然后可能将结果返回给请求者。
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("MyTomcat starts");
            while (true) {
                //Socket是网络间两个进程通信的端点
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                //利用MyRequest封装request对象
                MyRequest myRequest = new MyRequest(inputStream);
                //利用MyResponse封装response对象
                MyResponse myResponse = new MyResponse(outputStream);
                //请求分发
                dispatch(myRequest,myResponse);
                //关闭socket连接
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据request和response进行消息的分发
     * @param myRequest
     * @param myResponse
     */
    private void dispatch(MyRequest myRequest, MyResponse myResponse) {
        //过滤浏览器的favicon.ico请求,否则报错，因为没有对应的servletMapping
        if(!myRequest.getUrl().equals("/favicon.ico")){
            //通过request中的url精确定位对应的servlet
            String clazz = urlServletMap.get(myRequest.getUrl());
            //通过反射调用该servlet，进行分发
            try {
                //获取Class对象
                Class<MyServlet> myServletClass = (Class<MyServlet>) Class.forName(clazz);
                //获取实例
                MyServlet myServlet = myServletClass.newInstance();
                //调用实例的方法进行输出
                myServlet.service(myRequest,myResponse);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化url和servletMapping，形成对应关系，根据url可以找到对应的servlet
     */
    private void initServletMapping() {
        for (ServletMapping servletMapping : ServletMappingConfig.servletMappingList) {
            urlServletMap.put(servletMapping.getUrl(),servletMapping.getClazz());
        }
    }
}
```
5. 准备测试servlet

```java
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
```

```java
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
```

6. 开启myTomcat服务器，进行访问

运行MyTomcat类的main方法，启动服务器，在浏览器输入url访问
![image](https://note.youdao.com/yws/public/resource/593c01edf75491c49f2952dd3b0229ae/xmlnote/21BE6A87BDD24DFB97F689F955444FE2/9769)

---

![image](https://note.youdao.com/yws/public/resource/593c01edf75491c49f2952dd3b0229ae/xmlnote/488F176B6B674998BE9DB2CF28872374/9766)

可以根据不同的url进行相应的响应，手写简易Tomcat完成