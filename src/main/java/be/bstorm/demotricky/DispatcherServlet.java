package be.bstorm.demotricky;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import be.bstorm.demotricky.di.DIContainer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "dispacherServlet", value = "/*", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {


    DIContainer diContainer = new DIContainer();

    @Override
    public void init(){
        diContainer.initController();
        diContainer.initRoutes();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map.Entry<Class<?>,String> action = diContainer.getAction(request.getRequestURI().replace("/demoTricky",""),"GET");
        Object controller = diContainer.getController(action.getKey());

        try {
            Method actionMethod = action.getKey().getMethod(action.getValue());
            List<String> result = (List<String>) actionMethod.invoke(controller);

            response.getWriter().println(result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}