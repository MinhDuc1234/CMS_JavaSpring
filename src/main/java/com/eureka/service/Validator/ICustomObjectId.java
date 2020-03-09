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
@Constraint(validatedBy = { ICustomObjectId.CustomObjectIdValidator.class,
        ICustomObjectId.CustomObjectIdsValidator.class, ICustomObjectId.LookupCustomObjectIdValidator.class })
public @interface ICustomObjectId {

    String message() default "Not availid Object ID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    abstract class BaseCustomObjectIdValidator<T> implements ConstraintValidator<ICustomObjectId, T> {
        protected Pattern r;

        @Override
        public void initialize(ICustomObjectId annotation) {
            this.r = Pattern.compile("([a-zA-Z0-9]+)");
        }

        protected Boolean isValid(String str) {
            if (this.r.matcher(str).lookingAt())
                return true;
            return false;
        }
    }

    class LookupCustomObjectIdValidator extends BaseCustomObjectIdValidator<Map<String, String>> {
        @Override
        public boolean isValid(Map<String, String> value, ConstraintValidatorContext context) {
            if (value == null)
                return true;
            for (Map.Entry<String, String> set : value.entrySet()) {
                if (!this.isValid(set.getValue()))
                    return false;
            }
            return true;
        }
    }

    class CustomObjectIdsValidator extends BaseCustomObjectIdValidator<List<String>> {
        @Override
        public boolean isValid(List<String> values, ConstraintValidatorContext context) {
            for (String string : values) {
                if (!this.isValid(string))
                    return false;
            }
            return true;
        }
    }

    class CustomObjectIdValidator extends BaseCustomObjectIdValidator<String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (!this.isValid(value))
                return false;
            return true;
        }
    }

}