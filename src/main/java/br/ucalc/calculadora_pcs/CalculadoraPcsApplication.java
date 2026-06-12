package br.ucalc.calculadora_pcs;

import br.ucalc.calculadora_pcs.service.IndiceImportacaoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CalculadoraPcsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalculadoraPcsApplication.class, args);
	}

	@Bean
	CommandLineRunner executarImportacao(
			IndiceImportacaoService service) {

		return args -> {

			service.importar("arquivos/indices.xlsx");

		};
	}
}
