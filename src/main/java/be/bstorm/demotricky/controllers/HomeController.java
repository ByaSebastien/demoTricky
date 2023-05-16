package be.bstorm.demotricky.controllers;

import be.bstorm.demotricky.annotations.Controller;
import be.bstorm.demotricky.annotations.GetMapping;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller(path="/home")
public class HomeController {

//    public HomeController(HomeService homeService){
//
//    }

    @GetMapping()
    public List<String> getAll(){
        return new ArrayList<>(Arrays.asList("Toto","Philipe"));
    }
}
