package com.example.firstservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/first-service")
@Slf4j
public class FirstServiceController {
    Environment env;

//    @Value("local.server.port")
//    private String serverPort;
    @Autowired
    public FirstServiceController(Environment env){
        this.env = env;
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "Welcome to the First service.";
    }

    @GetMapping("/message")
    public String message(@RequestHeader(value="first-request", required = true) String header){
        System.out.println(header);
        log.info(header);
        return "Hello, First service.";
    }

    @GetMapping("/check")
    public String check(HttpServletRequest request) {
        log.info("server port={}", request.getServerPort());

        return String.format("Hi, there. This is a message from First service on PORT %s",
                env.getProperty("local.server.port"));
    }
}
