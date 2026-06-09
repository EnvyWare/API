package com.envyful.api.scanning;

import java.util.Objects;

public class ClassReference {

    private final String className;

    public ClassReference(String className) {
        this.className = className;
    }

    public String className() {
        return this.className;
    }

    public static ClassReference of(String className) {
        return new ClassReference(className);
    }

    public static ClassReference of(Class<?> clazz) {
        return new ClassReference(clazz.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ClassReference)) {
            return false;
        }

        ClassReference that = (ClassReference) o;
        return Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(className);
    }
}
