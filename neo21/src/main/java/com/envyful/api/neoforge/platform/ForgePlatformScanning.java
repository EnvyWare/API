package com.envyful.api.neoforge.platform;

import com.envyful.api.scanning.*;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ForgePlatformScanning implements PlatformScanning {

    public static final ForgePlatformScanning INSTANCE = new ForgePlatformScanning();

    @Override
    public List<ScannedClass> scan(ClassScanQuery query) {
        Map<String, ScannedClass.Builder> scannedClasses = new LinkedHashMap<>();

        for (var data : ModList.get().getAllScanData()) {
            this.collectClassData(scannedClasses, data);
            this.collectAnnotationData(scannedClasses, data);
        }

        List<ScannedClass> results = new ArrayList<>();

        for (var mutableClass : scannedClasses.values()) {
            var scannedClass = mutableClass.build();

            if (this.matches(scannedClass, query)) {
                results.add(scannedClass);
            }
        }

        return results;
    }

    private void collectClassData(
            Map<String, ScannedClass.Builder> scannedClasses,
            ModFileScanData data
    ) {
        for (var classData : data.getClasses()) {
            var className = classData.clazz().getClassName();
            var scannedClass = scannedClasses.computeIfAbsent(className, ScannedClass::builder);

            for (var iface : classData.interfaces()) {
                scannedClass.addInterface(iface.getClassName());
            }
        }
    }

    private void collectAnnotationData(
            Map<String, ScannedClass.Builder> scannedClasses,
            ModFileScanData data
    ) {
        for (var annotationData : data.getAnnotations()) {
            if (annotationData.targetType() != ElementType.TYPE) {
                continue;
            }

            var className = annotationData.clazz().getClassName();
            var scannedClass = scannedClasses.computeIfAbsent(className, ScannedClass::builder);

            scannedClass.addAnnotation(new ScannedAnnotation(
                    ClassReference.of(annotationData.annotationType().getClassName()),
                    Map.copyOf(annotationData.annotationData())
            ));
        }
    }

    private boolean matches(ScannedClass scannedClass, ClassScanQuery query) {
        for (var requiredAnnotation : query.requiredAnnotations()) {
            if (scannedClass.annotation(requiredAnnotation) == null) {
                return false;
            }
        }

        for (var requiredInterface : query.requiredInterfaces()) {
            if (!scannedClass.interfaces().contains(requiredInterface)) {
                return false;
            }
        }

        for (var requiredSuperclass : query.requiredSuperclasses()) {
            if (!scannedClass.superclasses().contains(requiredSuperclass)) {
                return false;
            }
        }

        return true;
    }
}
