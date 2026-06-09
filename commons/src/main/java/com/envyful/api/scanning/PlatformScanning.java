package com.envyful.api.scanning;

import java.util.List;

public interface PlatformScanning {

    List<ScannedClass> scan(ClassScanQuery query);

    default ClassScanQuery.Builder query() {
        return new ClassScanQuery.Builder(this);
    }

}
