package com.pp.couchdblib.couchbase.lite;

import com.pp.couchdblib.couchbase.lite.internal.RevisionInternal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Context passed into a Validator.
 */
public interface ValidationContext {


}



class ValidationContextImpl implements ValidationContext {

    private Database database;
    private RevisionInternal currentRevision;
    private RevisionInternal newRev;
    private String rejectMessage;
    private List<String> changedKeys;

    ValidationContextImpl(Database database, RevisionInternal currentRevision, RevisionInternal newRev) {
        this.database = database;
        this.currentRevision = currentRevision;
        this.newRev = newRev;
    }


    String getRejectMessage() {
        return rejectMessage;
    }
}
