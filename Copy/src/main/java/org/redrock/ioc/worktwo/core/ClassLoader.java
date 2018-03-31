package org.redrock.ioc.worktwo.core;

import com.sun.org.apache.bcel.internal.util.ClassSet;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.redrock.ioc.worktwo.annotation.Component;
import org.redrock.ioc.worktwo.annotation.Controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * 类加载器
 * 加载worktwo下的所有类和方法
 */
public class ClassLoader {
    private static final String packageName = "org.redrock.ioc.worktwo";

    private Set<Class<?>> classSet;
    private Set<Class<?>> controllerSet;
    private Set<Class<?>> componentSet;

    public ClassLoader() {
        load();
    }

    /**
     * 通过packageName调用下方的其他方法加载所有文件
     */
    private void load() {
        classSet = new HashSet<Class<?>>();
        try {
            Enumeration<URL> resouces = Thread.currentThread().getContextClassLoader().getResources(packageName.replaceAll("\\.", "/"));
            if (!resouces.hasMoreElements()){
                System.out.println(packageName.replace("\\.", "/")+"加载失败");
            }else{
                System.out.println(packageName.replace("\\.", "/")+"加载成功");
            }
            while ((resouces.hasMoreElements())) {
                URL url = resouces.nextElement();
                String protocol = url.getProtocol();
                if (protocol.equalsIgnoreCase("file")) {
                    String packagePath = url.getPath();
                    loadClass(classSet, packageName, packagePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadComponentSet();
        loadControllerSet();
    }

    public void setControllerSet(Set<Class<?>> controllerSet) {
        this.controllerSet = controllerSet;
    }

    public void setComponentSet(Set<Class<?>> componentSet) {
        this.componentSet = componentSet;
    }

    public Set<Class<?>> getControllerSet() {
        return controllerSet;
    }

    public Set<Class<?>> getComponentSet() {
        return componentSet;
    }

    /**
     * 加载类的方法，像classset中加载类，三个参数，一个set集合，packagePath用于加载路径下的所有file类，packageName用于和file的name组合得到class类
     * @param classSet
     * @param packageName
     * @param packagePath
     */
    private void loadClass(Set<Class<?>> classSet, String packageName, String packagePath) {
        final File[] files = new File(packagePath).listFiles(new FileFilter() {

            public boolean accept(File pathname) {//只会加载class和文件夹
                return (pathname.isFile() && pathname.getName().endsWith(".class")) || pathname.isDirectory();
            }
        });
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {//如果是文件则直接使用getClass加载
                if (packageName != null && !packageName.equals("")) {
                    fileName = packageName  + "." +  fileName.substring(0, fileName.lastIndexOf("."));;
                }
                Class<?> clazz = getClass(fileName);
                classSet.add(clazz);
            } else {//如果是文件夹，就将文件名称作为新的路径，递归本方法加载文件名下所有文件
                String subPackageName = fileName;
                System.out.println("\n"+"<----------"+fileName+"文件夹开始加载---------->");
                if (packageName != null && !packageName.equals("")) {
                    subPackageName = packageName + "." + subPackageName;
                }
                String subPackagePath = fileName;
                if (packagePath != null && !packagePath.equals("")) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                loadClass(classSet, subPackageName, subPackagePath);
            }
        }
    }

    /**
     * 调用forName方法通过文件路径名加载类
     * @param fileName
     * @return
     */
    private Class<?> getClass(String fileName) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fileName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    /**
     * 从classSet中将带有component注释的.class文件取出来放入ComponentSet中
     */
    private void loadComponentSet() {
        componentSet = new HashSet<Class<?>>();
        if (classSet != null) {
            for (Class<?> clazz : classSet) {
                if (clazz.getAnnotation(Component.class) != null) {
                    componentSet.add(clazz);
                    System.out.println("已经将"+clazz.getName()+"加载到componentSet中");
                }
            }
        }
    }

    /**
     * 同上加载所有控制器
     */
    private void loadControllerSet() {
        controllerSet = new HashSet<Class<?>>();
        if (classSet != null) {
            for (Class<?> clazz : classSet) {
                if (clazz.getAnnotation(Controller.class) != null) {
                    System.out.println("准备将"+clazz.getName()+"加载到controllerSet中");
                    controllerSet.add(clazz);
                    System.out.println("已经将"+clazz.getName()+"加载到controllerSet中");
                }
            }
        }
    }
}
