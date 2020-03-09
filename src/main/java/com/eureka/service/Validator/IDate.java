package com.eureka.service.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import com.eureka.service.Config.ValueConfig;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IDate.DateValidator.class)
public @interface IDate {

    String message() default "Value: ${validatedValue} is invalid (format: \"{param}\")";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String param() default ValueConfig.DATE_FORMAT;

    class DateValidator implements ConstraintValidator<IDate, java.util.Date> {
        String format = "";

        @Override
        public void initialize(IDate annotation) {
            this.format = annotation.param();
        }

        @Override
        public boolean isValid(java.util.Date value, ConstraintValidatorContext context) {
            return true;
        }
    }

}
