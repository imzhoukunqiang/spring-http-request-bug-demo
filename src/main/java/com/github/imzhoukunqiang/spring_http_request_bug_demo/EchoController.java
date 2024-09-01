package com.github.imzhoukunqiang.spring_http_request_bug_demo;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EchoController {

    @PostMapping("/echo")
    public ResponseEntity<String> echo(RequestEntity<String> request) {
        return ResponseEntity.ok(request.getBody());
    }

}
