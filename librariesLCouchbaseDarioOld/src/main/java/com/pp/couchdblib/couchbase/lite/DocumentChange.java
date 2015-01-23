package com.pp.couchdblib.couchbase.lite;

import com.pp.couchdblib.couchbase.lite.internal.RevisionInternal;
import com.pp.couchdblib.couchbase.lite.internal.InterfaceAudience;

import java.net.URL;

public class DocumentChange {

    @InterfaceAudience.Private
    DocumentChange(RevisionInternal addedRevision, RevisionInternal winningRevision, boolean isConflict, URL sourceUrl) {
        this.addedRevision = addedRevision;
        this.winningRevision = winningRevision;
        this.isConflict = isConflict;
        this.sourceUrl = sourceUrl;
    }

    private RevisionInternal addedRevision;
    private RevisionInternal winningRevision;
    private boolean isConflict;
    private URL sourceUrl;

    public String getDocumentId() {
        return addedRevision.getDocId();
    }

    public URL getSourceUrl() {
        return sourceUrl;
    }

    @InterfaceAudience.Private
    public RevisionInternal getAddedRevision() {
        return addedRevision;
    }

    @InterfaceAudience.Private
    RevisionInternal getWinningRevision() {
        return winningRevision;
    }

}
