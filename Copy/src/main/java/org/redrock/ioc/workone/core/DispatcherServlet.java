package org.redrock.ioc.workone.core;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
@WebServlet("/*")
public class DispatcherServlet extends HttpServlet{
    Map<String,Object> urlControllerMaps =new HashMap<>();

    /**
     * 初始化controller的map实例化容器
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        ControllerLoader loader=new ControllerLoader();
        urlControllerMaps=loader.load();
    }
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] uri={"index","index"};

        //reqUri得到的第一个数字为请求的servlet的名字，第二个参数所要调用的方法
        System.out.println("reqUri-------------->"+req.getRequestURI().substring(1));
        String[] reqUri=req.getRequestURI().substring(1).split("/");

        if (reqUri!=null&&reqUri.length>0){
            for (int i = 0; i < 2&&i<reqUri.length; i++) {
                uri[i]=reqUri[i];
            }
        }

        String controllerName=uri[0].toLowerCase();
        System.out.println("你所要请求的控制器名为-------->"+controllerName);

        String methodName=uri[1].toLowerCase();
        System.out.println("你所要请求的方法名为-------->"+methodName);

        //从urlControllerMaps中得到控制器的实例化对象，然后得到下属的所有方法
        Object object=urlControllerMaps.get(controllerName)==null?urlControllerMaps.get("index"):urlControllerMaps.get(controllerName);
        Method[] methods=object.getClass().getDeclaredMethods();
        //遍历控制器下的所有方法，将req的uri中指定的方法进行实例化
        for (Method method:methods){
            if (method.getName().equalsIgnoreCase(methodName)){
                try{
                    method.invoke(object,req,resp);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
