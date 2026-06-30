package br.ucalc.calculadora_pcs.config;

import br.ucalc.calculadora_pcs.repository.IndiceEconomicoRepository;
import br.ucalc.calculadora_pcs.service.IndiceImportacaoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CargaInicial implements CommandLineRunner {

    private final IndiceImportacaoService indiceImportacaoService;
    private final IndiceEconomicoRepository repository;

    public CargaInicial(IndiceImportacaoService indiceImportacaoService,
                        IndiceEconomicoRepository repository) {
        this.indiceImportacaoService = indiceImportacaoService;
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {

        repository.deleteAll();

        indiceImportacaoService.importar("arquivos/indices.xlsx");

        System.out.println("Índices importados com sucesso.");
    }
}
