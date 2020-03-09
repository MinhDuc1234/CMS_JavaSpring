package com.eureka.service.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import com.eureka.service.Core.FkError;
import com.eureka.service.Entity.BaseEntity;
import com.eureka.service.Service.ForeignKeyService;

import org.springframework.beans.factory.annotation.Autowired;

@Target({ ElementType.TYPE, ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { IFk.FkValidator.class })
public @interface IFk {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    public abstract class BaseFkValidator<T> implements ConstraintValidator<IFk, T> {

        @Autowired
        protected ForeignKeyService foreignKeyService;

        protected Boolean customValid(List<FkError> fkErrors, ConstraintValidatorContext context) {
            if (fkErrors.size() > 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(String.join("", fkErrors.stream().map(t -> {
                    return "[" + t.getFieldName() + ": (" + t.getCodes().toString() + ") not exists" + "]";
                }).collect(Collectors.toList()))).addConstraintViolation();
                return false;
            }
            return true;
        }

    }

    public class FkValidator extends BaseFkValidator<Object> {

        @Override
        @SuppressWarnings("unchecked")
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            List<BaseEntity> list = new ArrayList<>();
            if (value instanceof List) {
                for (Object t : ((List<Object>) value)) {
                    if (t == null)
                        continue;
                    list.add((BaseEntity) t);
                }
            } else {
                if (value != null) {
                    list.add((BaseEntity) value);
                }
            }
            if (list.size() == 0)
                return true;
            List<FkError> fkErrors = this.foreignKeyService.validate(list);
            return super.customValid(fkErrors, context);
        }

    }
}