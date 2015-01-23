package com.pp.couchdblib.couchbase.lite.replicator;

import com.pp.couchdblib.couchbase.lite.internal.InterfaceAudience;
import com.pp.couchdblib.couchbase.lite.support.HttpClientFactory;

import java.util.Map;

@InterfaceAudience.Private
public interface ChangeTrackerClient extends HttpClientFactory {

    void changeTrackerReceivedChange(Map<String,Object> change);

    void changeTrackerStopped(ChangeTracker tracker);
}
