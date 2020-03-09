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
@Constraint(validatedBy = { IId.IdValidator.class, IId.IdsValidator.class, IId.IdsFilterValidator.class })
public @interface IId {

    String message() default "Wrong ID format '([a-zA-Z0-9\\-_]+)'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    public abstract class BaseIdValidator<T> implements ConstraintValidator<IId, T> {

        protected Pattern r;

        @Override
        public void initialize(IId annotation) {
            this.r = Pattern.compile("^([a-zA-Z0-9\\-_]+)$");
        }

        protected Boolean isValid(String str) {
            if (this.r.matcher(str).lookingAt())
                return true;
            return false;
        }

    }

    public class IdValidator extends BaseIdValidator<String> {

        @Override
        public boolean isValid(String str, ConstraintValidatorContext context) {
            if (str == null)
                return true;
            return this.isValid(str);
        }

    }

    public class IdsValidator extends BaseIdValidator<List<String>> {

        @Override
        public boolean isValid(List<String> strs, ConstraintValidatorContext context) {
            if (strs == null)
                return true;
            for (String str : strs) {
                if (!this.isValid(str))
                    return false;
            }
            return true;
        }

    }

    public class IdsFilterValidator extends BaseIdValidator<Map<String, List<String>>> {

        @Override
        public boolean isValid(Map<String, List<String>> map, ConstraintValidatorContext context) {
            if (map == null)
                return true;
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                for (String str : entry.getValue()) {
                    if (!this.isValid(str))
                        return false;
                }
            }
            return true;
        }

    }

}