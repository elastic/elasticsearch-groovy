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
package org.elasticsearch.groovy.client.action.deletebyquery

import org.elasticsearch.action.deletebyquery.DeleteByQueryRequest
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder
import org.elasticsearch.client.Client

/**
 * {@code DeleteByQueryRequestExtensions} provides Groovy-friendly {@link DeleteByQueryRequest} extensions.
 * @see Client#deleteByQuery(DeleteByQueryRequest)
 */
class DeleteByQueryRequestExtensions {
    /**
     * Sets the content query {@code source} to use to delete data.
     *
     * @param self The {@code this} reference for the {@link DeleteByQueryRequest}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static DeleteByQueryRequest source(DeleteByQueryRequest self, Closure source) {
        self.source(source.asJsonBytes())
    }

    /**
     * Sets the content query {@code source} to use to delete data.
     *
     * @param self The {@code this} reference for the {@link DeleteByQueryRequest}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static DeleteByQueryRequest setSource(DeleteByQueryRequest self, Closure source) {
        source(self, source)
    }

    /**
     * Sets the content query {@code source} to use to delete data.
     *
     * @param self The {@code this} reference for the {@link DeleteByQueryRequestBuilder}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static DeleteByQueryRequestBuilder setSource(DeleteByQueryRequestBuilder self, Closure source) {
        self.setSource(source.asJsonBytes())
    }
}