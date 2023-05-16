package be.bstorm.demotricky.di;

import be.bstorm.demotricky.annotations.Controller;
import be.bstorm.demotricky.annotations.GetMapping;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class DIContainer {

    private Map<Class<?>,Object> controllers = new HashMap<>();
    private Map<Class<?>, Object> services = new HashMap<>();
    //          URL         VERB             CONTROLLER  METHOD
    private Map<String, Map<String,Map.Entry<Class<?>,String>>> routesMapping = new HashMap<>();

    public void initController(){

        Reflections reflections = new Reflections("be.bstorm.demotricky");

        Set<Class<?>> controllerType = reflections.getTypesAnnotatedWith(Controller.class);

        controllerType.forEach(System.out::println);

        for (Class<?> ctrlClass : controllerType){
            //creer / instancier les controllers
            if(!this.controllers.containsKey(ctrlClass)){
                this.createController(ctrlClass);
            }
        }

    }

    public void initRoutes(){

        for(Map.Entry<Class<?>,Object> entry : this.controllers.entrySet()){

            Class<?> type = entry.getKey();
            Method[] methods = type.getDeclaredMethods();
            Controller ctrl = type.getAnnotation(Controller.class);

            for(Method method : methods){

                String path = null;
                String verb = null;
                Map.Entry<Class<?>,String> action;

                if(method.isAnnotationPresent(GetMapping.class)){
                    GetMapping annotation = method.getAnnotation(GetMapping.class);
                    path = ctrl.path();
                    if(!annotation.path().equals("/")){
                        path += annotation.path();
                    }
                    verb = "GET";
                }

                action = Map.entry(type,method.getName());

                if(!this.routesMapping.containsKey(path)){
                    this.routesMapping.put(path,new HashMap<>());
                }

                this.routesMapping.get(path).put(verb,action);
            }
        }
    }

    public Map.Entry<Class<?>,String> getAction(String path,String verb){

        return this.routesMapping.get(path).get(verb);
    }

    public Object getController(Class<?> tClass){
        return this.controllers.get(tClass);
    }

    public void createController(Class<?> controller){

        Constructor<?> constructor = controller.getConstructors()[0];

        List<Object> params = new ArrayList<>();
        Parameter[] parameters = constructor.getParameters();

        for(Parameter parameter : parameters){

            Class<?> tparam = parameter.getType();

            if(!services.containsKey(tparam)){
                //creer / instancier Service
                this.createService(tparam);
            }

            Object instance = this.services.get(tparam);
            params.add(instance);
        }
        try {
            Object newInstance = constructor.newInstance(params.toArray());
            this.controllers.put(controller,newInstance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void createService(Class<?> service){

        Constructor<?> constructor = service.getConstructors()[0];

        List<Object> params = new ArrayList<>();

        Parameter[] parameters = constructor.getParameters();

        for (Parameter parameter : parameters){

            Class<?> tParam = parameter.getType();

            if(!services.containsKey(tParam)){

                this.createService(tParam);
            }

            Object instance = this.services.get(tParam);
            params.add(instance);

        }
        try {
            Object newInstance = constructor.newInstance(params.toArray());
            this.services.put(service,newInstance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
