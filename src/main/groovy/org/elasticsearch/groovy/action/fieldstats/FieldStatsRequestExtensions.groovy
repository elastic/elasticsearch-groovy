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
package org.elasticsearch.groovy.action.fieldstats

import org.elasticsearch.action.fieldstats.FieldStatsRequest
import org.elasticsearch.client.Client
import org.elasticsearch.client.Requests

/**
 * {@code FieldStatsRequestExtensions} provides Groovy-friendly {@link FieldStatsRequest} extensions.
 * @see Client#fieldStats(FieldStatsRequest)
 */
class FieldStatsRequestExtensions {
    /**
     * Sets the content {@code source} to handle field stats.
     *
     * @param self The {@code this} reference for the {@link FieldStatsRequest}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static FieldStatsRequest source(FieldStatsRequest self, Closure source) {
        self.source(source.build(Requests.CONTENT_TYPE).bytes())
    }
}