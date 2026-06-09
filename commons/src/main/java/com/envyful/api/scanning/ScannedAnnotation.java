package com.envyful.api.scanning;

import java.util.Map;

public final class ScannedAnnotation {

    private final ClassReference type;
    private final Map<String, Object> values;

    public ScannedAnnotation(ClassReference type, Map<String, Object> values) {
        this.type = type;
        this.values = Map.copyOf(values);
    }

    public ClassReference type() {
        return this.type;
    }

    public Map<String, Object> values() {
        return this.values;
    }

    public String string(String key) {
        var value = this.values.get(key);

        if (value instanceof String) {
            return (String) value;
        }

        return null;
    }
}