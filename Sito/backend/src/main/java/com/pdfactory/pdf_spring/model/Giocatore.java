package com.pdfactory.pdf_spring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Giocatore")
public class Giocatore
{
    @Id
    private Long id;
    private String nome;
}
