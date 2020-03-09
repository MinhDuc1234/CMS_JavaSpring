package com.eureka.service.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import com.eureka.service.Core.Type.DateTime;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IDateTime.DateTimeValidator.class)
public @interface IDateTime {

    String message() default "Invalid date time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class DateTimeValidator implements ConstraintValidator<IDateTime, DateTime> {
        @Override
        public boolean isValid(DateTime value, ConstraintValidatorContext context) {
            return true;
        }
    }

}
