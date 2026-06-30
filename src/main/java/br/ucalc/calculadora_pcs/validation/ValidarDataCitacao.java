package br.ucalc.calculadora_pcs.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = DataCitacaoValidator.class)
@Documented
public @interface ValidarDataCitacao {

    String message() default "Dados da citação inválidos.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
