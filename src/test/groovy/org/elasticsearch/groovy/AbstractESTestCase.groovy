/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.groovy

import groovy.transform.CompileStatic
import org.elasticsearch.test.ESTestCase

/**
 * The basis for all Groovy client test classes that do not require a running Elasticsearch cluster / client.
 * @see AbstractESIntegTestCase
 */
@CompileStatic
abstract class AbstractESTestCase extends ESTestCase {
    /**
     * Sanitize the test code to allow Groovy to cleanup after itself rather than forcing the onus onto everyone else.
     */
    static {
        assert GroovyTestSanitizer.groovySanitized
    }
}
