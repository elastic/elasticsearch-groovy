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
package org.elasticsearch.groovy.action.admin.indices.mapping.put

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder
import org.elasticsearch.client.IndicesAdminClient

/**
 * {@code PutMappingRequestExtensions} provides Groovy-friendly {@link PutMappingRequest} extensions.
 * @see IndicesAdminClient#putMapping(PutMappingRequest)
 */
class PutMappingRequestExtensions {
    /**
     * Sets the mapping {@code source}.
     *
     * @param self The {@code this} reference for the {@link PutMappingRequest}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static PutMappingRequest source(PutMappingRequest self, Closure source) {
        self.source(source.asJsonString())
    }

    /**
     * Sets the mapping {@code source}.
     *
     * @param self The {@code this} reference for the {@link PutMappingRequestBuilder}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static PutMappingRequestBuilder setSource(PutMappingRequestBuilder self, Closure source) {
        self.setSource(source.asJsonString())
    }
}