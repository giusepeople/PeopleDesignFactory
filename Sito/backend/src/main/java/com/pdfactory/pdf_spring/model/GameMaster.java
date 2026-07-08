package com.pdfactory.pdf_spring.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
@Table(name = "GameMaster")
public class GameMaster
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(name = "password", nullable = false)
    private String password;
}
