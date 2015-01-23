package com.pp.couchdblib.couchbase.lite;

import com.pp.couchdblib.couchbase.lite.internal.InterfaceAudience;
import com.pp.couchdblib.couchbase.lite.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnsavedRevision extends Revision {

    private Map<String, Object> properties;

    /**
     * Constructor
     */
    @InterfaceAudience.Private
    /* package */ protected UnsavedRevision(Document document, SavedRevision parentRevision) {

        super(document);

        if (parentRevision == null) {
            parentRevID = null;
        } else {
            parentRevID = parentRevision.getId();
        }

        Map<String, Object> parentRevisionProperties;

        if (parentRevision == null) {
            parentRevisionProperties = null;
        } else {
            parentRevisionProperties = parentRevision.getProperties();
        }

        if (parentRevisionProperties == null) {
            properties = new HashMap<String, Object>();
            properties.put("_id", document.getId());
            if (parentRevID != null) {
                properties.put("_rev", parentRevID);
            }
        }
        else {
            properties = new HashMap<String, Object>(parentRevisionProperties);
        }

    }

    /**
     * Get the id of the owning document.  In the case of an unsaved revision, may return null.
     * @return
     */
    @Override
    @InterfaceAudience.Public
    public String getId() {
        return null;
    }

    /**
     * Set the properties for this revision
     */
    @InterfaceAudience.Public
    public void setProperties(Map<String,Object> properties) {
        this.properties = properties;
    }

    /**
     * Saves the new revision to the database.
     *
     * This will throw an exception with a 412 error if its parent (the revision it was created from)
     * is not the current revision of the document.
     *
     * Afterwards you should use the returned Revision instead of this object.
     *
     * @return A new Revision representing the saved form of the revision.
     * @throws CouchbaseLiteException
     */
    @InterfaceAudience.Public
    public SavedRevision save() throws CouchbaseLiteException {
        boolean allowConflict = false;
        return document.putProperties(properties, parentRevID, allowConflict);
    }

    /**
     * A special variant of -save: that always adds the revision, even if its parent is not the
     * current revision of the document.
     *
     * This can be used to resolve conflicts, or to create them. If you're not certain that's what you
     * want to do, you should use the regular -save: method instead.
     */
    @InterfaceAudience.Public
    public SavedRevision save(boolean allowConflict) throws CouchbaseLiteException {
        return document.putProperties(properties, parentRevID, allowConflict);
    }

    @Override
    @InterfaceAudience.Public
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    @InterfaceAudience.Public
    public SavedRevision getParentRevision() {
        if (parentRevID == null || parentRevID.length() == 0) {
            return null;
        }
        return document.getRevision(parentRevID);
    }

    @Override
    @InterfaceAudience.Public
    public List<SavedRevision> getRevisionHistory() throws CouchbaseLiteException {
        // (Don't include self in the array, because this revision doesn't really exist yet)
        SavedRevision parent = getParentRevision();
        return parent != null ? parent.getRevisionHistory() : new ArrayList<SavedRevision>();
    }



}
