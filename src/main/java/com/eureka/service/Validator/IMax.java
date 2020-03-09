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
@Constraint(validatedBy = IMax.MaxValidator.class)
public @interface IMax {

    String message() default "Value: ${validatedValue}, Max: {param}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int param();

    class MaxValidator implements ConstraintValidator<IMax, Integer> {
        int max = 0;

        @Override
        public void initialize(IMax annotation) {
            this.max = annotation.param();
        }

        @Override
        public boolean isValid(Integer value, ConstraintValidatorContext context) {
            if (value == null)
                return true;
            if (value > this.max) {
                return false;
            }
            return true;
        }
    }

}