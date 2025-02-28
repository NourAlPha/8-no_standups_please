package com.example.Controller;

import org.springframework.web.bind.annotation.*;

// http://localhost:8080/swagger-ui/index.html
// http://localhost:8080/v3/api-docs
@RestController
@RequestMapping("/api/test")  // Base path for all endpoints
public class SwaggerTestController {
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, Swagger!";
    }

    @GetMapping("/greet/{name}")
    public String greetUser(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    @PostMapping("/echo")
    public String echoMessage(@RequestBody String message) {
        return "You said: " + message;
    }
}
