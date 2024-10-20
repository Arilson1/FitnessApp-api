package com.arilson.fitness.fitness_app_api.repository;

import com.arilson.fitness.fitness_app_api.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPessoaRepository extends JpaRepository<Pessoa, Long> {
}
