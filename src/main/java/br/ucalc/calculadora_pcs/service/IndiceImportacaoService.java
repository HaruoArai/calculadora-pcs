package br.ucalc.calculadora_pcs.service;

import br.ucalc.calculadora_pcs.model.IndiceEconomico;
import br.ucalc.calculadora_pcs.model.enums.TipoIndice;
import br.ucalc.calculadora_pcs.repository.IndiceEconomicoRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndiceImportacaoService {

    private final IndiceEconomicoRepository repository;

    public IndiceImportacaoService(IndiceEconomicoRepository repository) {
        this.repository = repository;
    }

    public void importarDoClasspath(String caminhoArquivo) throws Exception {

        ClassPathResource resource = new ClassPathResource(caminhoArquivo);

        if (!resource.exists()) {
            throw new IllegalStateException(
                    "Planilha de índices não encontrada: " + caminhoArquivo
            );
        }

        try (
                InputStream inputStream = resource.getInputStream();
                Workbook workbook = WorkbookFactory.create(inputStream)
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            List<IndiceEconomico> indices = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String tipoTexto =
                        formatter.formatCellValue(row.getCell(0)).trim();

                String referenciaTexto =
                        formatter.formatCellValue(row.getCell(1)).trim();

                String valorTexto =
                        formatter.formatCellValue(row.getCell(2)).trim();

                if (tipoTexto.isEmpty()
                        || referenciaTexto.isEmpty()
                        || valorTexto.isEmpty()) {
                    continue;
                }

                TipoIndice tipo = TipoIndice.valueOf(
                        tipoTexto.toUpperCase()
                );

                YearMonth referencia =
                        YearMonth.parse(referenciaTexto);

                BigDecimal valor =
                        converterValor(row.getCell(2), valorTexto);

                IndiceEconomico indice = new IndiceEconomico();
                indice.setTipo(tipo);
                indice.setReferencia(referencia);
                indice.setValor(valor);

                indices.add(indice);
            }

            repository.saveAll(indices);

            System.out.println(
                    indices.size() + " índices econômicos importados."
            );
        }
    }

    private BigDecimal converterValor(
            Cell cell,
            String valorTexto
    ) {
        if (cell != null
                && cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(
                    cell.getNumericCellValue()
            );
        }

        String valorNormalizado = valorTexto
                .replace(".", "")
                .replace(",", ".");

        return new BigDecimal(valorNormalizado);
    }
}