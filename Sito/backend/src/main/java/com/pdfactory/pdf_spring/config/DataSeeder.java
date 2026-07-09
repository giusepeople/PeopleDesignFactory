package com.pdfactory.pdf_spring.config;

import com.pdfactory.pdf_spring.model.GameMaster;
import com.pdfactory.pdf_spring.repository.GameMasterRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final GameMasterRepository gameMasterRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(GameMasterRepository gameMasterRepository, PasswordEncoder passwordEncoder) {
        this.gameMasterRepository = gameMasterRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (gameMasterRepository.findByNome("gamemaster").isEmpty()) {
            GameMaster gm = new GameMaster();
            gm.setNome("root");
            gm.setPassword(passwordEncoder.encode("root"));
            gameMasterRepository.save(gm);
            System.out.println("GM di default creato -> username: root / password: root!");
        }
    }
}