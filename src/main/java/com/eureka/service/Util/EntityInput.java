package com.eureka.service.Util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Id;

import com.eureka.service.Core.UI.Column;
import com.eureka.service.Core.UI.Column.ColumnType;
import com.eureka.service.Core.UI.Column.MinMaxValue;
import com.eureka.service.Interface.Enum.IEnum;
import com.eureka.service.Core.UI.Input;
import com.eureka.service.Core.UI.Validate;
import com.eureka.service.Validator.IId;
import com.eureka.service.Validator.IMaxLength;
import com.eureka.service.Validator.IMinLength;
import com.eureka.service.Validator.IRequired;
import com.eureka.service.Validator.IValueRange;
import com.eureka.service.Validator.Input.IInputSelect;
import com.eureka.service.Validator.Input.IInputSelectStatic;
import com.eureka.service.Validator.Setting.IInputLabel;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EntityInput {

    public static List<Field> getFields(Class<?> type) {
        Class<?> t = type;
        List<Field> fields = new ArrayList<>();
        while (t != null) {
            fields.addAll(Arrays.stream(t.getDeclaredFields()).collect(Collectors.toList()));
            t = t.getSuperclass();
        }
        return fields;
    }

    public static String getPrimaryKey(List<Field> fields) {
        for (Field field : fields) {
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                return field.getName();
            }
        }
        return "id";
    }

    public static Boolean getManualPrimaryKey(List<Field> fields) {
        for (Field field : fields) {
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                return false;
            }
        }
        return true;
    }

    public static List<Input> getInputs(List<Field> fields) {
        List<Input> inputs = new ArrayList<>();
        for (Field e : fields) {
            if (Modifier.isStatic(e.getModifiers()))
                continue;

            Input input = new Input();
            input.setName(e.getName());

            List<Validate> validates = new ArrayList<>();

            List<Annotation> settingAnnotations = Stream.of(e.getAnnotations())
                    .filter(annotation -> "com.eureka.service.Validator.Setting"
                            .equals(annotation.annotationType().getPackage().getName()))
                    .collect(Collectors.toList());
            if (settingAnnotations.stream().filter(_e -> "IInputIgnore".equals(_e.annotationType().getSimpleName()))
                    .count() > 0)
                continue;

            settingAnnotations.forEach(setting -> {
                Class<? extends Annotation> aClass = setting.annotationType();
                InvocationHandler invocationHandler = Proxy.getInvocationHandler((Proxy) setting);
                Object value = null;
                try {
                    Method paramMethod = aClass.getDeclaredMethod("value");
                    value = invocationHandler.invoke(invocationHandler, paramMethod, null);
                } catch (Throwable e1) {
                }

                switch (setting.annotationType().getSimpleName()) {
                case "IInputUISize":
                    input.setSize(value == null ? 6 : (Integer) value);
                    break;

                case "IInputMulti":
                    input.setMulti(true);
                    break;

                case "IInputLabel":
                    input.setLabel(value == null ? "" : (String) value);
                    break;

                case "IInputHint":
                    input.setHint(value == null ? "" : (String) value);
                    break;

                case "IInputDisableOnUpdate":
                    input.setDisableUpdate(true);
                    break;

                case "IInputDisable":
                    input.setDisable(true);
                    break;

                case "IInputSort":
                    input.setCanSort(true);
                    break;
                }
            });

            Optional<Annotation> optionAnnotation = Stream.of(e.getAnnotations())
                    .filter(annotation -> "com.eureka.service.Validator.Input"
                            .equals(annotation.annotationType().getPackage().getName()))
                    .findFirst();
            if (!optionAnnotation.isPresent())
                continue;

            Annotation inpuAnnotation = optionAnnotation.get();
            input.setType(inpuAnnotation.annotationType().getSimpleName());
            Class<? extends Annotation> aClass = inpuAnnotation.annotationType();
            InvocationHandler invocationHandler = Proxy.getInvocationHandler((Proxy) inpuAnnotation);
            Object value = null;
            try {
                Method paramMethod = aClass.getDeclaredMethod("param");
                value = invocationHandler.invoke(invocationHandler, paramMethod, null);
                input.setParam(value);
            } catch (Throwable e1) {
            }

            List<Annotation> annotations = Stream.of(e.getAnnotations())
                    .filter(annotation -> "com.eureka.service.Validator"
                            .equals(annotation.annotationType().getPackage().getName()))
                    .collect(Collectors.toList());

            for (Annotation f : annotations) {
                aClass = f.annotationType();
                Validate validate = new Validate();
                validate.setName(aClass.getSimpleName());

                invocationHandler = Proxy.getInvocationHandler((Proxy) f);
                try {
                    Method paramMethod = aClass.getDeclaredMethod("param");
                    validate.setParam(invocationHandler.invoke(invocationHandler, paramMethod, null));
                } catch (Throwable e1) {
                }
                try {
                    Method messageMethod = aClass.getDeclaredMethod("message");
                    String message = invocationHandler.invoke(invocationHandler, messageMethod, null).toString();
                    if (message.contains("{param}")) {
                        message = message.replace("{param}", validate.getParam().toString());
                    }
                    validate.setMessage(message);
                } catch (Throwable e1) {
                }
                validates.add(validate);
            }
            input.setValidates(validates);
            inputs.add(input);
        }
        return inputs;
    }

    public static List<Column> getColumns(List<Field> fields) {

        Set<String> defaultFields = new HashSet<>(Arrays.asList("updatedBy", "changedBy", "createdBy", "isDelete",
                "changedDate", "createdDate", "version", "display"));

        List<Column> columns = new ArrayList<>();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()))
                continue;
            if (defaultFields.contains(field.getName()))
                continue;
            if (field.getAnnotation(IRequired.class) == null && field.getAnnotation(Id.class) != null)
                continue;
            if (field.getAnnotation(JsonIgnore.class) != null)
                continue;

            Column column = new Column();

            if (field.getAnnotation(IRequired.class) != null || field.getAnnotation(Id.class) != null)
                column.setNullable(false);

            IInputLabel iInputLabel = field.getAnnotation(IInputLabel.class);
            if (iInputLabel != null) {
                column.setName(iInputLabel.value(), false);
            } else {
                column.setName(field.getName(), true);
            }

            column.setFieldName(field.getName());
            IInputSelect iInputSelect = field.getAnnotation(IInputSelect.class);
            IInputSelectStatic iInputSelectStatic = field.getAnnotation(IInputSelectStatic.class);

            if (iInputSelect != null) {
                column.setColumnType(ColumnType.SELECT);
                column.setParam(Arrays.asList(iInputSelect.param()));
            } else if (iInputSelectStatic != null) {
                column.setColumnType(ColumnType.SELECT);
                column.setParam(Arrays.asList(iInputSelectStatic.param()));
            } else {
                switch (field.getType().getName()) {
                case "java.lang.String":
                    MinMaxValue minMaxValue = new MinMaxValue();
                    IMinLength iMinLength = field.getAnnotation(IMinLength.class);
                    IMaxLength iMaxLength = field.getAnnotation(IMaxLength.class);
                    if (iMinLength != null)
                        minMaxValue.setMin(iMinLength.param());
                    if (iMaxLength != null)
                        minMaxValue.setMax(iMaxLength.param());
                    column.setParam(minMaxValue);
                    column.setColumnType(ColumnType.TEXT);
                    break;

                case "java.lang.Integer":
                case "java.lang.Long":
                    column.setColumnType(ColumnType.NUMBER);
                    break;
                case "java.lang.Double":
                case "java.lang.Float":
                    column.setColumnType(ColumnType.DECIMAL);
                    break;

                case "java.sql.Date":
                case "java.util.Date":
                    column.setColumnType(ColumnType.DATE);
                    break;

                case "java.lang.Boolean":
                    column.setColumnType(ColumnType.SELECT);
                    column.setParam(Arrays.asList("true", "false"));
                    break;

                default:
                    column.setColumnType(ColumnType.NONE);
                    // System.out.println("#" + field.getType().getName());
                    break;
                }
            }
            columns.add(column);
        }
        return columns;
    }

    public static Set<String> getForeignField(List<Field> fields) {
        return fields.stream().filter(t -> t.getAnnotation(IId.class) != null)
                .filter(t -> t.getAnnotation(Id.class) == null).map(t -> t.getName()).collect(Collectors.toSet());
    }

    public static Map<String, Class<?>> getEnumField(List<Field> fields) {
        Map<String, Class<?>> map = new HashMap<>();
        fields.stream()
                .filter(t -> t.getAnnotation(IValueRange.class) != null
                        || t.getAnnotation(IInputSelectStatic.class) != null
                        || t.getAnnotation(IInputSelect.class) != null)
                .forEach(t -> {
                    if (IEnum.class.isAssignableFrom(t.getType())) {
                        map.put(t.getName(), t.getType());
                    } else {
                        map.put(t.getName(), null);
                    }
                });
        return map;
    }

    public static Set<String> getBooleanField(List<Field> fields) {
        return fields.stream().filter(t -> t.getType().getName().equals("java.lang.Boolean")).map(t -> t.getName())
                .collect(Collectors.toSet());
    }

}