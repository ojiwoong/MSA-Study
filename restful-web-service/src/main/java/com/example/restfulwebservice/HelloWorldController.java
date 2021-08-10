package com.example.restfulwebservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @GetMapping("/hello-world")
    public String helloWord(){
        return "Hello Wordl";
    }



    @GetMapping("/hello-world-bean")
    public HelloWorldBean helloWordBean(){
        return new HelloWorldBean("Hello Wordl");
    }

    @GetMapping("/hello-world-bean/path-variable/{name}/{age}")
    public HelloWorldBean helloWordBean(@PathVariable String name, @PathVariable int age){
        return new HelloWorldBean(String.format("Hello Wordl, name = %s, age = %d", name, age) );
    }
}
