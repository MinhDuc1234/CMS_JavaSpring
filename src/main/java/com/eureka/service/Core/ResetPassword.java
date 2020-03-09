package com.eureka.service.Core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import com.eureka.service.Validator.IRequired;
import com.eureka.service.Validator.IUsername;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResetPassword extends Password {
    @IRequired
    @ApiModelProperty(required = true)
    @IUsername
    private String username;
    @IRequired
    @ApiModelProperty(required = true)
    private String activationCode;

    @Target({ ElementType.FIELD, ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = ResetPasswordValidator.class)
    public @interface IResetPassword {
        String message() default "";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static class ResetPasswordValidator implements ConstraintValidator<IResetPassword, ResetPassword> {

        @Override
        public void initialize(IResetPassword annotation) {

        }

        @Override
        public boolean isValid(ResetPassword value, ConstraintValidatorContext context) {
            return value.isValid();
        }

    }
}