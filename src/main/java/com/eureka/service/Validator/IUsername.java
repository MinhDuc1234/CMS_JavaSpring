package com.eureka.service.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IUsername.UsernameValidator.class)
public @interface IUsername {

    String message() default "Value: ${validatedValue} is not an valid username (regexp: '^([a-zA-Z0-9-.]{5,})$')";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String param() default "^([a-zA-Z0-9-.]{5,})$";

    class UsernameValidator implements ConstraintValidator<IUsername, String> {
        Pattern pattern;

        @Override
        public void initialize(IUsername annotation) {
            this.pattern = Pattern.compile(annotation.param());
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null)
                return true;
            if (this.pattern.matcher(value).find()) {
                return true;
            }
            return false;
        }
    }

}