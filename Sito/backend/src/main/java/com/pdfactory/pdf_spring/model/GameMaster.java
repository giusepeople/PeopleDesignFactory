package com.pdfactory.pdf_spring.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Getter
@Setter
@Table(name = "GameMaster")
public class GameMaster
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "gameMaster")
    private List<Partita> games = new ArrayList<>();
}
