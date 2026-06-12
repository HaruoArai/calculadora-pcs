package br.ucalc.calculadora_pcs.repository;

import br.ucalc.calculadora_pcs.model.Calculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalculoRepository extends JpaRepository<Calculo, Long> {
}
