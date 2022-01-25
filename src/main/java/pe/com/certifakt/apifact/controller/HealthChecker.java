package pe.com.certifakt.apifact.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HealthChecker {
    @GetMapping("/")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body("Ok 3");
    }
}
