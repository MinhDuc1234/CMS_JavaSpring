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
@Constraint(validatedBy = ITime.TimeValidator.class)
public @interface ITime {

    String message() default "Value: ${validatedValue} is invalid (format: \"{param}\")";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String param() default "hh:mm";

    class TimeValidator implements ConstraintValidator<ITime, java.sql.Time> {
        String format = "";

        @Override
        public void initialize(ITime annotation) {
            this.format = annotation.param();
        }

        @Override
        public boolean isValid(java.sql.Time value, ConstraintValidatorContext context) {
            return true;
        }
    }

}