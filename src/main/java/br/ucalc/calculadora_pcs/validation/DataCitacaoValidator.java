package br.ucalc.calculadora_pcs.validation;

import br.ucalc.calculadora_pcs.dto.CalculoFormDTO;
import br.ucalc.calculadora_pcs.model.enums.TipoEmenda;
import br.ucalc.calculadora_pcs.model.enums.TipoJuros;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DataCitacaoValidator
        implements ConstraintValidator<ValidarDataCitacao, CalculoFormDTO> {

    @Override
    public boolean isValid(CalculoFormDTO form,
                           ConstraintValidatorContext context) {

        if (form == null) {
            return true;
        }

        TipoJuros tipoJuros = form.getTipoJuros();

        if (tipoJuros == null || tipoJuros == TipoJuros.SEM_JUROS) {
            return true;
        }

        LocalDate dataCitacao = form.getDataCitacao();
        LocalDate dataAtualizacao = form.getDataAtualizacao();
        TipoEmenda tipoEmenda = form.getTipoEmenda();

        if (dataCitacao == null) {
            adicionarErro(context, "Informe a data da citação");
            return false;
        }

        if (dataAtualizacao == null) {
            return true;
        }

        if (tipoEmenda != null && tipoEmenda != TipoEmenda.NENHUMA) {
            LocalDate limiteEmenda = LocalDate.of(2021, 12, 8);

            if (dataCitacao.isAfter(limiteEmenda)) {
                adicionarErro(
                        context,
                        "Com regra constitucional aplicada, a data da citação não pode ser posterior a 08/12/2021"
                );
                return false;
            }

            return true;
        }

        if (dataCitacao.isAfter(dataAtualizacao)) {
            adicionarErro(
                    context,
                    "A data da citação não pode ser posterior à data final da atualização"
            );
            return false;
        }

        return true;
    }

    private void adicionarErro(ConstraintValidatorContext context,
                               String mensagem) {

        context.disableDefaultConstraintViolation();

        context.buildConstraintViolationWithTemplate(mensagem)
                .addPropertyNode("dataCitacao")
                .addConstraintViolation();
    }
}
