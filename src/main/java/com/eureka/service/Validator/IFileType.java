package com.eureka.service.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import org.springframework.web.multipart.MultipartFile;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { IFileType.FileTypeValidator.class, IFileType.FilesTypeValidator.class })
public @interface IFileType {

    String message() default "Mime Types: {param}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] param() default {};

    abstract class BaseFileTypeValidator<T> implements ConstraintValidator<IFileType, T> {
        private Set<String> typeSet = new HashSet<>();

        @Override
        public void initialize(IFileType constraintAnnotation) {
            ConstraintValidator.super.initialize(constraintAnnotation);
            for (String type : constraintAnnotation.param()) {
                this.typeSet.add(type);
            }
        }

        public boolean isValid(MultipartFile value) {
            if (this.typeSet.contains(value.getContentType()))
                return true;
            System.out.println(value.getContentType());
            return false;
        }
    }

    class FileTypeValidator extends BaseFileTypeValidator<MultipartFile> {
        @Override
        public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
            if (this.isValid(value))
                return true;
            return false;
        }
    }

    class FilesTypeValidator extends BaseFileTypeValidator<MultipartFile[]> {
        @Override
        public boolean isValid(MultipartFile[] values, ConstraintValidatorContext context) {
            for (MultipartFile file : values)
                if (!this.isValid(file))
                    return false;
            return true;
        }
    }

}