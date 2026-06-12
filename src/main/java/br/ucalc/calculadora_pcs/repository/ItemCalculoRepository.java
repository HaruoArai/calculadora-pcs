package br.ucalc.calculadora_pcs.repository;

import br.ucalc.calculadora_pcs.model.ItemCalculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCalculoRepository extends JpaRepository<ItemCalculo, Long> {
}
