package com.envyful.api.scanning;

import java.util.List;

public class NoOpPlatformScanning implements PlatformScanning {
    public static final NoOpPlatformScanning INSTANCE = new NoOpPlatformScanning();

    @Override
    public List<ScannedClass> scan(ClassScanQuery query) {
        return List.of();
    }
}
