package com.eureka.service.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IValueRange.ValueRangeValidator.class)
public @interface IValueRange {

    String message() default "Value: ${validatedValue} is not an valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] params() default {};

    class ValueRangeValidator implements ConstraintValidator<IValueRange, String> {
        Set<String> set = new HashSet<>();

        @Override
        public void initialize(IValueRange annotation) {
            for (String str : annotation.params()) {
                set.add(str);
            }
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null)
                return true;
            if (set.contains(value))
                return true;
            return false;
        }
    }

}