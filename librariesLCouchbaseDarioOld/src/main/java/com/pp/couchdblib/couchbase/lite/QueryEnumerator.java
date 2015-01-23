package com.pp.couchdblib.couchbase.lite;

import com.pp.couchdblib.couchbase.lite.internal.InterfaceAudience;

import java.util.Iterator;
import java.util.List;

/**
 * Enumerator on a Query's result rows.
 * The objects returned are instances of QueryRow.
 */
public class QueryEnumerator implements Iterator<QueryRow> {

    private Database database;
    private List<QueryRow> rows;
    private int nextRow;
    private long sequenceNumber;

    /**
     * Constructor
     */
    @InterfaceAudience.Private
    /* package */ QueryEnumerator(Database database, List<QueryRow> rows, long sequenceNumber) {
        this.database = database;
        this.rows = rows;
        this.sequenceNumber = sequenceNumber;

        // Fill in the rows' database reference now
        for (QueryRow row : rows) {
            row.setDatabase(database);
        }
    }

    /**
     * Constructor
     */
    @InterfaceAudience.Private
    /* package */ QueryEnumerator(QueryEnumerator other) {
        this.database = other.database;
        this.rows = other.rows;
        this.sequenceNumber = other.sequenceNumber;
    }

    /**
     * Gets the number of rows in the QueryEnumerator.
     */
    @InterfaceAudience.Public
    public int getCount() {
        return rows.size();
    }

    /**
     * Gets the next QueryRow from the results, or null
     * if there are no more results.
     */
    @Override
    @InterfaceAudience.Public
    public QueryRow next() {
        if (nextRow >= rows.size()) {
            return null;
        }
        return rows.get(nextRow++);
    }


    @Override
    @InterfaceAudience.Public
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryEnumerator that = (QueryEnumerator) o;

        if (rows != null ? !rows.equals(that.rows) : that.rows != null) return false;

        return true;
    }

    /**
     * Required to satisfy java Iterator interface
     */
    @Override
    @InterfaceAudience.Public
    public boolean hasNext() {
        return nextRow < rows.size();
    }

    /**
     * Required to satisfy java Iterator interface
     */
    @Override
    @InterfaceAudience.Public
    public void remove() {
        throw new UnsupportedOperationException("QueryEnumerator does not allow remove() to be called");
    }


    /**
     * Resets the enumeration so the next call to -nextObject or -nextRow will return the first row.
     */
    @InterfaceAudience.Public
    public void reset() {
        nextRow = 0;
    }


}
