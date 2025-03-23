package ru.ntoponen.dissertationdocumentgenerator.app.impl;

import org.springframework.stereotype.Component;
import ru.ntoponen.dissertationdocumentgenerator.domain.Gender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FieldValueExtractor {
    public Map<String, String> getFieldValuesUsingGetters(Object obj, String parentPrefix)
        throws InvocationTargetException, IllegalAccessException {
        Map<String, String> fieldValues = new HashMap<>();
        Class<?> clazz = obj.getClass();

        List<Method> methods = Arrays.stream(clazz.getMethods())
            .filter(method -> method.getName().startsWith("get") && method.getParameterCount() == 0)
            .filter(method -> !method.getName().equals("getClass"))
            .toList();

        for (Method method : methods) {
            String fieldName = method.getName().substring(3);
            fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
            String fullFieldName = (parentPrefix == null || parentPrefix.isEmpty()) ? fieldName : parentPrefix + "." + fieldName;

            Object fieldValue = method.invoke(obj);

            if (fieldValue != null) {
                if (isPrimitiveOrWrapper(fieldValue.getClass()) || fieldValue.getClass().equals(String.class)) {
                    fieldValues.put(fullFieldName, fieldValue.toString());
                } else {
                    fieldValues.putAll(getFieldValuesUsingGetters(fieldValue, fullFieldName));
                }
            } else {
                fieldValues.put(fullFieldName, "null");
            }
        }

        return fieldValues;
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || clazz.equals(Integer.class) || clazz.equals(Long.class) ||
            clazz.equals(Boolean.class) || clazz.equals(Double.class) ||
            clazz.equals(Float.class) || clazz.equals(Character.class) ||
            clazz.equals(Byte.class) || clazz.equals(Short.class) || clazz.equals(LocalDate.class) || clazz.equals(List.class) ||
            clazz.equals(Gender.class);
    }
}
