package com.pdfactory.pdf_spring.model;

import com.pdfactory.pdf_spring.enums.TipoDomanda;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "domanda")
@Getter
@Setter
@NoArgsConstructor
public class Domanda {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_template_id", nullable = false)
    private Modulo moduloTemplate;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDomanda type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    // solo per scelta multipla
    @Column(name = "options_json", columnDefinition = "TEXT")
    private String opzioneJson;

    @Column(name = "correct_option")
    private String opzioneCorretta;

    @Column(name = "hint_text", columnDefinition = "TEXT")
    private String hintText;

    // nullable: se valorizzato, solo chi ha questo ruolo puo' compilare il campo
    // (lato UI); l'invio finale resta comunque a carico del PM/QA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restricted_role_id")
    private Ruolo restrictedRole;
}