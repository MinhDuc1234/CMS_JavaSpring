package com.eureka.service.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IPerm.PermValidator.class)
public @interface IPerm {
    String message() default "Value: ${validatedValue}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class PermValidator implements ConstraintValidator<IPerm, String> {
        @Override
        public void initialize(IPerm annotation) {
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return true;
        }
    }

}