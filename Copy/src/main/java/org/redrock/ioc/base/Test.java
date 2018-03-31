package org.redrock.ioc.base;

public class Test {
    public static void main(String[] args) {

    }
    public static void testClass(){
        Class<?> helloClass=Hello.class;
    }
    public static void testClassForName(){
        try{
            Class.forName("org.redrock.ioc.base.Hello");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void cast(){
        Class<?> clazz=null;
        try{
            clazz=Class.forName("org.redrock.ioc.base.Student");
            Object o=clazz.newInstance();
            try{
                Student student_one = (Student) o;
            }catch (ClassCastException e){
                e.printStackTrace();
            }
            if (o.getClass().getCanonicalName().equals("org.redrock.ioc.base.Student")){
                Student student_two= (Student) o;
            }
            if (o instanceof Student){
                Student student_three=(Student) o;
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    class Student{
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
