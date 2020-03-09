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
@Constraint(validatedBy = IEmail.EmailValidator.class)
public @interface IEmail {

    String message() default "Value: ${validatedValue} is not an valid email";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String param() default "^\\s*[\\w\\-\\+_]+(\\.[\\w\\-\\+_]+)*\\@[\\w\\-\\+_]+\\.[\\w\\-\\+_]+(\\.[\\w\\-\\+_]+)*\\s*$";

    class EmailValidator implements ConstraintValidator<IEmail, String> {
        Pattern pattern;

        @Override
        public void initialize(IEmail annotation) {
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