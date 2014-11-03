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
package org.elasticsearch.groovy.client.action.index

import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.client.Requests

/**
 * {@code IndexRequestExtensions} provides Groovy-friendly {@link IndexRequest} extensions.
 * @see Client#index(IndexRequest)
 */
class IndexRequestExtensions {
    /**
     * Sets the content {@code source} to index.
     *
     * @param self The {@code this} reference for the {@link IndexRequest}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static IndexRequest source(IndexRequest self, Closure source) {
        self.source(source.buildBytes(Requests.INDEX_CONTENT_TYPE))
    }

    /**
     * Sets the content {@code source} to index.
     *
     * @param self The {@code this} reference for the {@link IndexRequest}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static IndexRequest setSource(IndexRequest self, Closure source) {
        source(self, source)
    }

    /**
     * Sets the content {@code source} to index.
     *
     * @param self The {@code this} reference for the {@link IndexRequestBuilder}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static IndexRequestBuilder setSource(IndexRequestBuilder self, Closure source) {
        self.setSource(source.buildBytes(Requests.INDEX_CONTENT_TYPE))
    }
}