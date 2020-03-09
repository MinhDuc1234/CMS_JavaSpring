package com.eureka.service.Validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IMinLength.MinLengthValidator.class)
public @interface IMinLength {

    String message() default "Min length: {param}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int param();

    class MinLengthValidator implements ConstraintValidator<IMinLength, String> {
        int minLength = 0;

        @Override
        public void initialize(IMinLength annotation) {
            this.minLength = annotation.param();
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null)
                return true;
            if (value.length() < this.minLength) {
                return false;
            }
            return true;
        }
    }

}