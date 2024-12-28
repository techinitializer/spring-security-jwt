package com.techinitializer.spring_security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/secured")
    public ResponseEntity<String> securedEndpoint() {
        return ResponseEntity.ok(("This is a secured endpoint."));
    }

    @GetMapping("/unsecured")
    public ResponseEntity<String> unsecuredEndpoint() {
        return ResponseEntity.ok(("This is a unsecured endpoint."));
    }
}
