package br.ucalc.calculadora_pcs;

import br.ucalc.calculadora_pcs.repository.IndiceEconomicoRepository;
import br.ucalc.calculadora_pcs.service.IndiceImportacaoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CalculadoraPcsApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				CalculadoraPcsApplication.class,
				args
		);
	}

	@Bean
	CommandLineRunner importarIndices(
			IndiceImportacaoService importacaoService,
			IndiceEconomicoRepository repository
	) {
		return args -> {

			if (repository.count() == 0) {

				System.out.println(
						"Banco vazio. Iniciando importação dos índices..."
				);

				importacaoService.importarDoClasspath(
						"indices/indices.xlsx"
				);

				System.out.println(
						"Importação concluída com sucesso."
				);

			} else {

				System.out.println(
						"Índices já cadastrados. Importação ignorada."
				);
			}
		};
	}
}