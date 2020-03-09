package com.eureka.service.Converter;

import javax.persistence.AttributeConverter;

import com.eureka.service.Interface.Enum.IEnum;

import java.io.Serializable;

public abstract class EnumConverter<T extends Serializable, E extends Enum<E> & IEnum<T>>
        implements AttributeConverter<E, T> {
    private final Class<E> clazz;

    public EnumConverter(Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T convertToDatabaseColumn(E attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getValue();
    }

    @Override
    public E convertToEntityAttribute(T dbData) {
        if (dbData == null) {
            return null;
        }

        E[] enums = clazz.getEnumConstants();
        for (E e : enums) {
            if (e.getValue().equals(dbData)) {
                return e;
            }
        }

        return null;
    }

}
