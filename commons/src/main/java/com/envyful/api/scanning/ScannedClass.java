package com.envyful.api.scanning;

import java.util.ArrayList;
import java.util.List;

public final class ScannedClass {

    private final ClassReference type;
    private final List<ClassReference> interfaces;
    private final List<ClassReference> superclasses;
    private final List<ScannedAnnotation> annotations;

    public ScannedClass(
            ClassReference type,
            List<ClassReference> interfaces,
            List<ClassReference> superclasses,
            List<ScannedAnnotation> annotations
    ) {
        this.type = type;
        this.interfaces = List.copyOf(interfaces);
        this.superclasses = List.copyOf(superclasses);
        this.annotations = List.copyOf(annotations);
    }

    public ClassReference type() {
        return this.type;
    }

    public List<ClassReference> interfaces() {
        return this.interfaces;
    }

    public List<ClassReference> superclasses() {
        return this.superclasses;
    }

    public List<ScannedAnnotation> annotations() {
        return this.annotations;
    }

    public boolean hasAnnotation(Class<?> annotation) {
        return this.hasAnnotation(ClassReference.of(annotation));
    }

    public boolean hasAnnotation(ClassReference annotation) {
        for (var scannedAnnotation : this.annotations) {
            if (scannedAnnotation.type().equals(annotation)) {
                return true;
            }
        }

        return false;
    }

    public ScannedAnnotation annotation(Class<?> annotation) {
        return this.annotation(ClassReference.of(annotation));
    }

    public ScannedAnnotation annotation(ClassReference annotation) {
        for (var scannedAnnotation : this.annotations) {
            if (scannedAnnotation.type().equals(annotation)) {
                return scannedAnnotation;
            }
        }

        return null;
    }

    public <T> Class<? extends T> loadAs(Class<T> expectedType, ClassLoader classLoader) throws ClassNotFoundException {
        var rawClass = Class.forName(this.type.className(), true, classLoader);

        if (!expectedType.isAssignableFrom(rawClass)) {
            throw new IllegalArgumentException(
                    rawClass.getName() + " does not implement/extend " + expectedType.getName()
            );
        }

        return rawClass.asSubclass(expectedType);
    }

    public static Builder builder(String type) {
        return new Builder(ClassReference.of(type));
    }

    public static Builder builder(ClassReference type) {
        return new Builder(type);
    }

    public static class Builder {

        private final ClassReference type;
        private final List<ClassReference> interfaces = new ArrayList<>();
        private final List<ClassReference> superclasses = new ArrayList<>();
        private final List<ScannedAnnotation> annotations = new ArrayList<>();

        private Builder(ClassReference type) {
            this.type = type;
        }

        public Builder addInterface(String interfaceType) {
            return this.addInterface(ClassReference.of(interfaceType));
        }

        public Builder addInterface(ClassReference interfaceType) {
            this.interfaces.add(interfaceType);
            return this;
        }

        public Builder addSuperclass(String superclass) {
            return this.addSuperclass(ClassReference.of(superclass));
        }

        public Builder addSuperclass(ClassReference superclass) {
            this.superclasses.add(superclass);
            return this;
        }

        public Builder addAnnotation(ScannedAnnotation annotation) {
            this.annotations.add(annotation);
            return this;
        }

        public ScannedClass build() {
            return new ScannedClass(
                    this.type,
                    this.interfaces,
                    this.superclasses,
                    this.annotations
            );
        }
    }
}
