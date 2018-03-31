package org.redrock.ioc.worktwo.core;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

//@WebServlet("/*")
//拦截所有请求
public class DispatcherServlet extends GenericServlet {
    private Map<Class<?>,Object> controllers;
    private Map<String,Method> handlers;

    /**
     * 初始化所有控制器对象和方法的实例化map
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        System.out.println("DispatcherServlet开始初始化");
        ClassLoader classLoader=new ClassLoader();
        BeanFactory beanFactory=new BeanFactory(classLoader);
        controllers =beanFactory.getControllers();
        handlers =beanFactory.getHandlers();
    }
//话说长的真的有点像过滤器
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        //第一步，转化req和resp
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;

        //获得请求方法和req的uri并将其组合，然后通过两者进行组合，从BeanFactory中
        //早已将默认的方法通过这种方法组合，这样就可以得到控制器中的方法
        System.out.println("request.getMethod:"+request.getMethod()+"\trequest.getRequestURI:"+request.getRequestURI());
        String handlerKey=request.getMethod()+":"+request.getRequestURI();
        Method method =handlers.get(handlerKey);
        if (method!=null) {
            System.out.println("所寻找到的方法为----------->" + method.getName());
        }else {
            System.out.println("未找到方法名"+handlerKey);
        }
        //通过方法找到方法所属的控制器对象
        Object controller=controllers.get(method.getDeclaringClass());

        System.out.println("所寻找到的控制器为--------->"+controller);

        if (controller!=null){
            try{//调用方法处理请求
                method.invoke(controller,request,response);
                System.out.println(method.getName()+"调用成功");
            } catch (IllegalAccessException |InvocationTargetException e){
                Throwable cause = e.getCause();
                if (cause instanceof  IOException){
                    IOException exception= (IOException) cause;
                    exception.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }
}
