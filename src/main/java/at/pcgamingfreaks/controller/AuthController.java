package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.model.dto.LoginRequestDTO;
import at.pcgamingfreaks.model.dto.LoginResponseDTO;
import at.pcgamingfreaks.model.dto.SignupRequestDTO;
import at.pcgamingfreaks.model.dto.SignupResponseDTO;
import at.pcgamingfreaks.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        log.info("Login request from {}", request.getUsername());
        return ResponseEntity.ok(authService.authenticate(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupRequestDTO request) {
        log.info("Signup request from {}", request.getUsername());
        return ResponseEntity.ok(authService.signup(request));
    }
}