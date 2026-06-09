package com.envyful.api.scanning;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ClassScanQuery {

    private final List<ClassReference> requiredAnnotations;
    private final List<ClassReference> requiredInterfaces;
    private final List<ClassReference> requiredSuperclasses;

    public ClassScanQuery(List<ClassReference> requiredAnnotations, List<ClassReference> requiredInterfaces, List<ClassReference> requiredSuperclasses) {
        this.requiredAnnotations = requiredAnnotations;
        this.requiredInterfaces = requiredInterfaces;
        this.requiredSuperclasses = requiredSuperclasses;
    }

    public List<ClassReference> requiredAnnotations() {
        return this.requiredAnnotations;
    }

    public List<ClassReference> requiredInterfaces() {
        return this.requiredInterfaces;
    }

    public List<ClassReference> requiredSuperclasses() {
        return this.requiredSuperclasses;
    }

    public static class Builder {

        private final PlatformScanning scanning;

        private List<ClassReference> requiredAnnotations = new ArrayList<>();
        private List<ClassReference> requiredInterfaces = new ArrayList<>();
        private List<ClassReference> requiredSuperclasses = new ArrayList<>();

        public Builder(PlatformScanning scanning) {
            this.scanning = scanning;
        }

        public Builder requireAnnotation(Class<? extends Annotation> annotation) {
            return this.requireAnnotation(ClassReference.of(annotation));
        }

        public Builder requireAnnotation(ClassReference annotation) {
            this.requiredAnnotations.add(annotation);
            return this;
        }

        public Builder requireAnnotations(ClassReference... annotations) {
            this.requiredAnnotations.addAll(List.of(annotations));
            return this;
        }

        public Builder requireInterface(Class<?> iface) {
            return this.requireInterface(ClassReference.of(iface));
        }

        public Builder requireInterface(ClassReference iface) {
            this.requiredInterfaces.add(iface);
            return this;
        }

        public Builder requireInterfaces(ClassReference... ifaces) {
            this.requiredInterfaces.addAll(List.of(ifaces));
            return this;
        }

        public Builder requireSuperclass(ClassReference superclass) {
            this.requiredSuperclasses.add(superclass);
            return this;
        }

        public Builder requireSuperclasses(ClassReference... superclasses) {
            this.requiredSuperclasses.addAll(List.of(superclasses));
            return this;
        }

        public ClassScanQuery build() {
            return new ClassScanQuery(this.requiredAnnotations, this.requiredInterfaces, this.requiredSuperclasses);
        }

        public List<ScannedClass> scan() {
            return this.scanning.scan(this.build());
        }
    }
}
