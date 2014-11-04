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
package org.elasticsearch.groovy.action.percolate

import org.elasticsearch.action.percolate.PercolateRequest
import org.elasticsearch.action.percolate.PercolateRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.client.Requests

/**
 * {@code PercolateRequestExtensions} provides Groovy-friendly {@link PercolateRequest} extensions.
 * @see Client#percolate(PercolateRequest)
 */
class PercolateRequestExtensions {
    /**
     * Sets the content {@code source} to percolate.
     *
     * @param self The {@code this} reference for the {@link PercolateRequest}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static PercolateRequest source(PercolateRequest self, Closure source) {
        self.source(source.buildBytes(Requests.INDEX_CONTENT_TYPE))
    }

    /**
     * Sets the content {@code source} to percolate.
     *
     * @param self The {@code this} reference for the {@link PercolateRequestBuilder}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static PercolateRequestBuilder setSource(PercolateRequestBuilder self, Closure source) {
        self.setSource(source.buildBytes(Requests.INDEX_CONTENT_TYPE))
    }
}