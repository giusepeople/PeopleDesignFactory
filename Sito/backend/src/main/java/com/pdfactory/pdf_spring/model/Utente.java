package com.pdfactory.pdf_spring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Utente")
public class Utente
{
    @Id
    private Long id;
    private String name;
}
