package org.redrock.ioc.workone.core;


import javax.servlet.annotation.WebServlet;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class ControllerLoader  {
    /**
     * 首先先将controller中的所有类加入到classSet之中
     * 然后将这些类进行实例化放入Map之中
     * @return Map<String,Object>
     */
    public Map<String,Object> load() {
        Set<Class<?>> classSet=new HashSet<>();
        try {
            String packageName = "org.redrock.ioc.workone.controller";
            Enumeration<URL> resources = ControllerLoader.class.getClassLoader().getResources(packageName.replaceAll("\\.", "/"));
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();
                if (protocol.equals("file")) {
                    String packagePath = resource.getPath();
                    addClass(classSet, packagePath, packageName);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Map<String,Object> urlControllersMap =new HashMap<>();
        for (Class<?> clazz:classSet){
            String className =clazz.getSimpleName();
            String controllerName=className.substring(0,className.lastIndexOf("Controller")).toLowerCase();
            System.out.println("加载控制器--------->"+controllerName);
            try{
                urlControllersMap.put(controllerName,clazz.newInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return urlControllersMap;
    }

    /**
     * @param classSet
     * @param packagePath
     * @param packageName
     * @throws ClassNotFoundException
     */
    private void addClass(Set<Class<?>> classSet, String packagePath, String packageName) throws ClassNotFoundException {
        File[] files=new File(packagePath).listFiles(pathname -> (pathname.isFile() && pathname.getName().endsWith(".class")) || pathname.isDirectory());
        for (File file:files){
            if (file.isFile()) {//如果是文件则直接将文件加载之后放入classSet之中
                String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
                if (packageName != null && !packageName.equals("")) {
                    fileName = packageName + "." + fileName;
                }
                Class<?> clazz = loadClass(fileName, false);
                classSet.add(clazz);
            }else {
                String pathName=file.getName();
                if (packageName!=null&&!packageName.equals("")){
                    packagePath =packagePath+"/"+pathName;
                    packageName =packageName+"."+pathName;
                }
                addClass(classSet,packagePath,packageName);
            }
        }
    }

    /**
     * 通过文件名称加载类
     * @param fileName
     * @param b
     * @return
     * @throws ClassNotFoundException
     */
    private Class<?> loadClass(String fileName, boolean b) throws ClassNotFoundException {
        Class<?> clazz=null;
        try {
             clazz = Class.forName(fileName, b, getClass().getClassLoader());
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return clazz;
    }

    /**
     * 获得..额。。没啥用吧
     * @param args
     */
    public static void main(String[] args) {
        ControllerLoader loader=new ControllerLoader();
        Map<String ,Object> urlControllerMaps=loader.load();
        Object object=urlControllerMaps.get("index");
        Method[] methods=object.getClass().getDeclaredMethods();
    }
}
