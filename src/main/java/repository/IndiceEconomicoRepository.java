package repository;

import br.ucalc.calculadora_pcs.model.IndiceEconomico;
import br.ucalc.calculadora_pcs.model.enums.TipoIndice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.Optional;

public interface IndiceEconomicoRepository extends JpaRepository<IndiceEconomico, Long> {

    Optional<IndiceEconomico> findByTipoAndReferencia(TipoIndice tipo, YearMonth referencia);
}
