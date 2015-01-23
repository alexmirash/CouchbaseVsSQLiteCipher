package com.couchbase.lite;

import com.couchbase.lite.internal.RevisionInternal;

import java.util.List;

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
