package com.github.hbq969.code.common.spring.yaml;

import lombok.Data;

@Data
public class TypePair {
    private String key;
    private Object value;
    private Class<?> type;

    public TypePair(String key, Object value, String type) {
        this.key = key;
        try {
            this.type = Class.forName(type);
        } catch (ClassNotFoundException e) {
            this.type = String.class;
        }
        try {
            if (this.type == Double.class || this.type == Float.class) {
                this.value = Double.valueOf(value.toString());
            } else if (this.type == Long.class || this.type == Integer.class
                    || this.type == Short.class || this.type == Byte.class) {
                this.value = Long.valueOf(value.toString());
            } else if (this.type == Character.class) {
                Integer i = Integer.valueOf(value.toString());
                char c = (char) i.intValue();
                this.value = c;
            } else if (this.type == Boolean.class) {
                this.value = Boolean.valueOf(value.toString());
            } else {
                this.value = value.toString();
            }
        } catch (Exception e) {
            this.value = String.valueOf(value);
            this.type = String.class;
        }
    }
}
