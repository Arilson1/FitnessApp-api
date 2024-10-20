package com.arilson.fitness.fitness_app_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pessoa")
@Data
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int idade;
    private double altura;
    private double peso;
    private double pesoDesejado;
    private String genero;

    @Enumerated(EnumType.STRING)
    private NivelAtividade nivelAtividade;

    @Enumerated(EnumType.STRING)
    private Objetivo objetivoPrincipal;

    private String alergias;
    private String preferenciasDieteticas;

    @Enumerated(EnumType.STRING)
    private NivelExperiencia nivelExperiencia;
}

enum NivelAtividade {
    SEDENTARIO,
    LEVEMENTE_ATIVO,
    MODERADAMENTE_ATIVO,
    MUITO_ATIVO
}

enum Objetivo {
    PERDA_GORDURA,
    GANHO_MASSA_MUSCULAR,
    MANUTENCAO_PESO,
    MELHORA_SAUDE_GERAL
}

enum NivelExperiencia {
    INICIANTE,
    INTERMEDIARIO,
    AVANCADO
}
