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
@Constraint(validatedBy = IPassword.PasswordValidator.class)
public @interface IPassword {

    String message() default "Password not strong enough (regexp: '^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*])(?=.{8,})')";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String param() default "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*])(?=.{8,})";

    class PasswordValidator implements ConstraintValidator<IPassword, String> {
        Pattern r;

        @Override
        public void initialize(IPassword annotation) {
            this.r = Pattern.compile(annotation.param());
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (this.r.matcher(value).lookingAt())
                return true;
            return false;
        }
    }

}