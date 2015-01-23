/**
 * Original iOS version by  Jens Alfke
 * Ported to Android by Marty Schoch
 *
 * Copyright (c) 2012 Couchbase, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.pp.couchdblib.couchbase.lite.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectWriter;

import com.pp.couchdblib.couchbase.lite.Manager;

/**
 * A request/response/document body, stored as either JSON or a Map<String,Object>
 */
public class Body {

    private byte[] json;
    private Object object;

    public Body(byte[] json) {
        this.json = json;
    }

    public Body(Map<String, Object> properties) {
        this.object = properties;
    }

    public Body(List<?> array) {
        this.object = array;
    }

    public byte[] getJson() {
        if (json == null) {
            lazyLoadJsonFromObject();
        }
        return json;
    }

    private void lazyLoadJsonFromObject() {
        if (object == null) {
            throw new IllegalStateException("Both json and object are null for this body: " + this);
        }
        try {
            json = Manager.getObjectMapper().writeValueAsBytes(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getObject() {
        if (object == null) {
            lazyLoadObjectFromJson();
        }
        return object;
    }

    private void lazyLoadObjectFromJson() {
        if (json == null) {
            throw new IllegalStateException("Both object and json are null for this body: " + this);
        }
        try {
            object = Manager.getObjectMapper().readValue(json, Object.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValidJSON() {
        if (object == null) {
            if (json == null) {
                throw new IllegalStateException("Both object and json are null for this body: " + this);
            }
            try {
                object = Manager.getObjectMapper().readValue(json, Object.class);
            } catch (IOException e) {
            }
        }
        return object != null;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getProperties() {
        Object object = getObject();
        if(object instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) object;
            return Collections.unmodifiableMap(map);
        }
        return null;
    }

    public Object getPropertyForKey(String key) {
        Map<String,Object> theProperties = getProperties();
        return theProperties.get(key);
    }

}
