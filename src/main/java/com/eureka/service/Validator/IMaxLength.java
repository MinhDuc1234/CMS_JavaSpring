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
@Constraint(validatedBy = IMaxLength.MaxLengthValidator.class)
public @interface IMaxLength {

    String message() default "Max length: {param}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int param();

    class MaxLengthValidator implements ConstraintValidator<IMaxLength, String> {
        int maxLength = 0;

        @Override
        public void initialize(IMaxLength annotation) {
            this.maxLength = annotation.param();
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null)
                return true;
            if (value.length() > this.maxLength) {
                return false;
            }
            return true;
        }
    }

}