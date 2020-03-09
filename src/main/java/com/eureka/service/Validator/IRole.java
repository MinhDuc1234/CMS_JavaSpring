package com.eureka.service.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import com.eureka.service.Enum.RoleEnum;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IRole.RoleValidator.class)
public @interface IRole {

    String message() default "Role not valid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String param() default "ROLE_ADMIN";

    class RoleValidator implements ConstraintValidator<IRole, RoleEnum> {
        @Override
        public boolean isValid(RoleEnum value, ConstraintValidatorContext context) {
            return true;
        }
    }

}