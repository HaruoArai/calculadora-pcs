package br.ucalc.calculadora_pcs.service;

import br.ucalc.calculadora_pcs.model.IndiceEconomico;
import br.ucalc.calculadora_pcs.model.enums.TipoIndice;
import br.ucalc.calculadora_pcs.repository.IndiceEconomicoRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.DataFormatter;
import java.util.ArrayList;
import java.util.List;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.YearMonth;

@Service
public class IndiceImportacaoService {

    private final IndiceEconomicoRepository repository;

    public IndiceImportacaoService(IndiceEconomicoRepository repository) {
        this.repository = repository;
    }

    public void importar(String caminhoArquivo) throws Exception {

        FileInputStream fis = new FileInputStream(caminhoArquivo);
        Workbook workbook = WorkbookFactory.create(fis);

        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();

        List<IndiceEconomico> indices = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);
            if (row == null) continue;

            String tipo = formatter.formatCellValue(row.getCell(0)).trim();
            String referencia = formatter.formatCellValue(row.getCell(1)).trim();

            if (tipo.isEmpty() || referencia.isEmpty()) continue;

            double valor = row.getCell(2).getNumericCellValue();

            IndiceEconomico indice = new IndiceEconomico();
            indice.setTipo(TipoIndice.valueOf(tipo));
            indice.setReferencia(YearMonth.parse(referencia));
            indice.setValor(BigDecimal.valueOf(valor));

            indices.add(indice);
        }

        repository.saveAll(indices);

        workbook.close();
        fis.close();
    }
}
