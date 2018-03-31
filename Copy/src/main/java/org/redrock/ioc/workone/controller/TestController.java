package org.redrock.ioc.workone.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TestController {
    public void test(HttpServletRequest request, HttpServletResponse response){
        try{
            response.getWriter().print("IOC");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
