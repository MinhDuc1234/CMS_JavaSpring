package com.eureka.service.Interface.Enum;

import java.io.Serializable;

public interface IEnum<T extends Serializable> {
    T getValue();

    public static <T extends Serializable, E extends Enum<E> & IEnum<T>> E parseValue(T value, Class<E> clazz) {
        E[] enums = clazz.getEnumConstants();
        for (E e : enums) {
            if (e.getValue().toString().equals(value.toString())) {
                return e;
            }
        }
        return null;
    }

    public static Object fromString(String value, Class<?> clazz) {
        Object[] enums = clazz.getEnumConstants();
        for (Object e : enums) {
            if (e.toString().equals(value)) {
                return e;
            }
        }
        return null;
    }

}
