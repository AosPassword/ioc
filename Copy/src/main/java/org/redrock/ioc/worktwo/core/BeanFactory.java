package org.redrock.ioc.worktwo.core;

import org.redrock.ioc.worktwo.annotation.Autowried;
import org.redrock.ioc.worktwo.annotation.RequestMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BeanFactory {
    private Map<Class<?>, Object> controllers;
    private Map<Class<?>, Object> components;
    private Map<String, Method> handlers;

    private ClassLoader classLoader;

    /**
     * 初始化上面那三个map
     * @param classLoader
     */
    public BeanFactory(ClassLoader classLoader) {
        this.classLoader = classLoader;
        initComponents();
        initControllersAndHandlers();
    }

    private void initControllersAndHandlers() {
        System.out.println("<----------BeanFactory开始实例化controllers以及其方法handlers---------->");
        Set<Class<?>> controllerSet = classLoader.getControllerSet();//先得到控制器的类
        controllers = new HashMap<>();
        handlers = new HashMap<>();
        for (Class<?> clazz : controllerSet) {//遍历所有控制器类
            try {
                String baseUri = clazz.getAnnotation(RequestMapping.class) != null ? clazz.getAnnotation(RequestMapping.class).value() : "";
                //将控制器类名下所有的有RequestMapping注释的类的名称
                Object controller =clazz.newInstance();//实例化控制器类
                Field[] fields=clazz.getDeclaredFields();//得到控制器名下的子类的属性
                for (Field field:fields){
                    if (field.getAnnotation(Autowried.class)!=null){//遍历控制器属性中含有Autowried注释的属性
                        Class<?> fieldClazz =field.getType();//得到属性的类
                        Object fieldValue =components.get(fieldClazz);//从components这个map中得到属性的实例化对象
                        if (field.getAnnotation(Autowried.class)!=null){
                            field.setAccessible(true);//设置属性的权限，如果是private也可以再之后更改属性
                        }
                        field.set(controller,fieldValue);//将控制器类的属性进行实例化
                    }
                }
                Method[] methods=clazz.getDeclaredMethods();//得到控制器下的所有方法,
                                                            // 让后将方法名和方法封装到handlers的集合中
                for (Method method:methods){
                    RequestMapping requestMapping=method.getAnnotation(RequestMapping.class);
                    //获得方法中的@RequestMapping中的参数，一个String，一个RequestMethod(获取请求方式)
                    if (requestMapping!=null){
                        //将String和RequestMethod中的请求方式用‘：’进行拼接，容易到DispatcherServlet中被检索
                        String requestUri = requestMapping.method().name() + ":" + baseUri + requestMapping.value();
                        handlers.put(requestUri,method);
                        System.out.println("已加载到方法名-------------->"+requestUri);
                    }
                }
                controllers.put(clazz,controller);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<Class<?>, Object> getControllers() {
        return controllers;
    }

    public Map<String, Method> getHandlers() {
        return handlers;
    }

    /**
     * 通过classLoader得到componentSet，实例化components，
     * 然后将class类和其对应的实例化对象封装到components这个map容器中
     */
    private void initComponents() {
        Set<Class<?>> componentSet = classLoader.getComponentSet();
        components = new HashMap<>();
        for (Class<?> clazz : componentSet) {
            try {
                Object object = clazz.newInstance();
                components.put(clazz, object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

    }
}
