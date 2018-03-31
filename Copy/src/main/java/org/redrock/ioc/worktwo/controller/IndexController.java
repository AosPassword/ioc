package org.redrock.ioc.worktwo.controller;

import org.redrock.ioc.worktwo.annotation.Autowried;
import org.redrock.ioc.worktwo.annotation.Controller;
import org.redrock.ioc.worktwo.annotation.RequestMapping;
import org.redrock.ioc.worktwo.annotation.RequestMethod;
import org.redrock.ioc.worktwo.component.World;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class IndexController {
    @Autowried
    World world;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public void test(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().print(world.test());
    }
}
