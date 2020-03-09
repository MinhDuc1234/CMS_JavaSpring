package com.eureka.service.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { IEnum.MapEnumValidator.class, IEnum.StringEnumValidator.class })
public @interface IEnum {

    String message() default "Not availid value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    abstract class BaseEnumValidator<T> implements ConstraintValidator<IEnum, T> {

        protected Pattern r;

        @Override
        public void initialize(IEnum annotation) {
            this.r = Pattern.compile("([a-zA-Z0-9_]+)");
        }

        protected Boolean isValid(String str) {
            if (this.r.matcher(str).lookingAt())
                return true;
            return false;
        }

    }

    class StringEnumValidator extends BaseEnumValidator<String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (this.isValid(value))
                return true;
            return false;
        }

    }

    class MapEnumValidator extends BaseEnumValidator<Map<String, List<String>>> {
        @Override
        public boolean isValid(Map<String, List<String>> value, ConstraintValidatorContext context) {
            if (value == null)
                return true;
            for (Map.Entry<String, List<String>> set : value.entrySet()) {
                for (String str : set.getValue()) {
                    if (!this.isValid(str))
                        return false;
                }
            }
            return true;
        }
    }

}