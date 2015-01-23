package com.pp.couchdblib.couchbase.lite.support;

import com.pp.couchdblib.couchbase.lite.Database;
import com.pp.couchdblib.couchbase.lite.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Utility that queues up objects until the queue fills up or a time interval elapses,
 * then passes all the objects at once to a client-supplied processor block.
 */
public class Batcher<T> {

    private ScheduledExecutorService workExecutor;
    private ScheduledFuture<?> flushFuture;
    private int capacity;
    private int delay;
    private List<T> inbox;
    private BatchProcessor<T> processor;
    private boolean shuttingDown = false;

    private Runnable processNowRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                processNow();
            } catch(Exception e) {
                // we don't want this to crash the batcher
                com.pp.couchdblib.couchbase.lite.util.Log.e(Database.TAG, "BatchProcessor throw exception", e);
            }
        }
    };

    public Batcher(ScheduledExecutorService workExecutor, int capacity, int delay, BatchProcessor<T> processor) {
        this.workExecutor = workExecutor;
        this.capacity = capacity;
        this.delay = delay;
        this.processor = processor;
    }

    public void processNow() {
        List<T> toProcess = null;
        synchronized(this) {
            if(inbox == null || inbox.size() == 0) {
                return;
            }
            toProcess = inbox;
            inbox = null;
            flushFuture = null;
        }
        if(toProcess != null) {
            processor.process(toProcess);
        }
    }

    public void queueObject(T object) {
        synchronized(this) {
            if(inbox != null && inbox.size() >= capacity) {
                flush();
            }
            if(inbox == null) {
                inbox = new ArrayList<T>();
                if(workExecutor != null) {
					flushFuture = workExecutor.schedule(processNowRunnable, delay, TimeUnit.MILLISECONDS);
                }
            }
            inbox.add(object);
        }
    }

    public void flush() {
        synchronized(this) {
            if(inbox != null) {
                boolean didcancel = false;
                if(flushFuture != null) {
                    didcancel = flushFuture.cancel(false);
                }
                //assume if we didn't cancel it was because it was already running
                if(didcancel) {
                    processNow();
                } else {
                    Log.v(Database.TAG, "skipping process now because didcancel false");
                }
            }
        }
    }

    public int count() {
        synchronized(this) {
            if(inbox == null) {
                return 0;
            }
            return inbox.size();
        }
    }

    public void close() {

    }

}
