package com.pp.couchdblib.couchbase.lite.support;

import java.util.Map;

public interface MultipartReaderDelegate {

    public void startedPart(Map<String, String> headers);

    public void appendToPart(byte[] data);

    public void finishedPart();

}
