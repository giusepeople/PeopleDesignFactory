package com.pdfactory.pdf_spring.controller;

import com.pdfactory.pdf_spring.dto.LoginRequest;
import com.pdfactory.pdf_spring.dto.LoginResponse;
import com.pdfactory.pdf_spring.model.GameMaster;
import com.pdfactory.pdf_spring.repository.GameMasterRepository;
import com.pdfactory.pdf_spring.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final GameMasterRepository gameMasterRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            GameMasterRepository gameMasterRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.gameMasterRepository = gameMasterRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        GameMaster gm = gameMasterRepository.findByNome(request.username()).orElse(null);

        if (gm == null || !passwordEncoder.matches(request.password(), gm.getPassword())) {
            return ResponseEntity.status(401).body("Credenziali non valide");
        }

        String token = jwtService.generateToken(gm.getNome());
        return ResponseEntity.ok(new LoginResponse(token, gm.getNome()));
    }
}