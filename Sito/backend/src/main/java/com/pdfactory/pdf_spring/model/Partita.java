package com.pdfactory.pdf_spring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Partita")
public class Partita
{
    @Id
    private Long id;
    private Long cod_part;
}
