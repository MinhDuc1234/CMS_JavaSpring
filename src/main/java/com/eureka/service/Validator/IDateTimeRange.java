package com.eureka.service.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import com.eureka.service.Core.Type.DateTimeRange;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IDateTimeRange.DateTimeRangeValidator.class)
public @interface IDateTimeRange {

    String message() default "Invalid range";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class DateTimeRangeValidator implements ConstraintValidator<IDateTimeRange, DateTimeRange> {
        @Override
        public boolean isValid(DateTimeRange value, ConstraintValidatorContext context) {
            return true;
        }
    }

}