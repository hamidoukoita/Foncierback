package com.example.foncierback.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Numéro de téléphone valide (déjà normalisé par {@link Telephones#normaliser}).
 * {@code maliUniquement = true} pour les comptes des sociétés (responsables, agents, admins) :
 * seuls les acquéreurs peuvent avoir un numéro étranger.
 * Une valeur nulle est acceptée : {@code @NotBlank} s'en charge.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Telephone.Validateur.class)
public @interface Telephone {

    boolean maliUniquement() default false;

    String message() default "Numéro de téléphone invalide";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validateur implements ConstraintValidator<Telephone, String> {

        private boolean maliUniquement;

        @Override
        public void initialize(Telephone annotation) {
            this.maliUniquement = annotation.maliUniquement();
        }

        @Override
        public boolean isValid(String valeur, ConstraintValidatorContext contexte) {
            if (valeur == null) return true;
            boolean valide = maliUniquement ? Telephones.estMalien(valeur) : Telephones.estValide(valeur);
            if (!valide) {
                contexte.disableDefaultConstraintViolation();
                contexte.buildConstraintViolationWithTemplate(Telephones.messageInvalide(valeur, maliUniquement)
                                .replace("{", "\\{").replace("$", "\\$"))
                        .addConstraintViolation();
            }
            return valide;
        }
    }
}
