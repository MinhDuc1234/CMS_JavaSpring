package com.eureka.service.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { IObjectId.ObjectIdValidator.class, IObjectId.ObjectIdsValidator.class })
public @interface IObjectId {

    String message() default "Not availid Object ID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    abstract class BaseObjectIdValidator<T> implements ConstraintValidator<IObjectId, T> {
        Pattern r;

        @Override
        public void initialize(IObjectId annotation) {
            this.r = Pattern.compile("([a-f0-9]{24})");
        }

        public boolean isValid(String value) {
            if (this.r.matcher(value).lookingAt())
                return true;
            return false;
        }
    }

    class ObjectIdsValidator extends BaseObjectIdValidator<List<String>> {
        @Override
        public boolean isValid(List<String> values, ConstraintValidatorContext context) {
            for (String string : values) {
                if (!this.isValid(string))
                    return false;
            }
            return true;
        }
    }

    class ObjectIdValidator extends BaseObjectIdValidator<String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (this.isValid(value))
                return true;
            return false;
        }
    }

}
