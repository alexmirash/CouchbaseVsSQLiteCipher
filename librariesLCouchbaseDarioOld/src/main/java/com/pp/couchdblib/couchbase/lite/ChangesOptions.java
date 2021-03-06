package com.pp.couchdblib.couchbase.lite;

import java.util.EnumSet;

import com.pp.couchdblib.couchbase.lite.Database.TDContentOptions;

/**
 * Options for _changes feed
 */
public class ChangesOptions {

    private int limit = Integer.MAX_VALUE;
    private EnumSet<TDContentOptions> contentOptions = EnumSet.noneOf(Database.TDContentOptions.class);
    private boolean includeDocs = false;
    private boolean includeConflicts = false;
    private boolean sortBySequence = true;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isIncludeConflicts() {
        return includeConflicts;
    }

    public boolean isIncludeDocs() {
        return includeDocs;
    }

    public boolean isSortBySequence() {
        return sortBySequence;
    }

    public EnumSet<TDContentOptions> getContentOptions() {
        return contentOptions;
    }
}
