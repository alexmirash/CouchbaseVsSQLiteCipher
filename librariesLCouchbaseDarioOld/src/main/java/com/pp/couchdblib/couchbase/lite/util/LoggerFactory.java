/**
 * Created by Wayne Carter.
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

package com.pp.couchdblib.couchbase.lite.util;

import java.util.Properties;
import java.util.ServiceLoader;

public class LoggerFactory {
    public static Logger createLogger() {
        // Attempt to load a Logger service.
        for (Logger logger : ServiceLoader.load(Logger.class)) {
            return logger;
        }

        // Choose Logger based on runtime.
        Properties properties = System.getProperties();
        String runtime = properties.getProperty("java.runtime.name");
        if (runtime != null) {
            if (runtime.toLowerCase().contains("android")) {
                return new AndroidLogger();
            }
        }

        // Return default System logger.
        return new SystemLogger();
    }
}
