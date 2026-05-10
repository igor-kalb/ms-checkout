package br.com.uri.mscheckout.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HealthCheck {
    
    @GetMapping("/health")
    public ResponseEntity<Void> liveness(){
        return ResponseEntity.ok(null);
    }

}
