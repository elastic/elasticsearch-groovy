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
package org.elasticsearch.groovy.action.update

import org.elasticsearch.action.update.UpdateRequest
import org.elasticsearch.action.update.UpdateRequestBuilder
import org.elasticsearch.client.Client

/**
 * {@code UpdateRequestExtensions} provides Groovy-friendly {@link UpdateRequest} extensions.
 * @see Client#update(UpdateRequest)
 */
class UpdateRequestExtensions {
    /**
     * Sets the content {@code docSource} (partial document) to use with the update.
     *
     * @param self The {@code this} reference for the {@link UpdateRequest}.
     * @param docSource The content params
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static UpdateRequest doc(UpdateRequest self, Closure docSource) throws Exception {
        self.doc(docSource.asMap())
    }

    /**
     * Sets the content {@code source} to update.
     *
     * @param self The {@code this} reference for the {@link UpdateRequest}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static UpdateRequest source(UpdateRequest self, Closure source) throws Exception {
        self.source(source.asJsonBytes())
    }

    /**
     * Sets the content {@code params} to use with the update.
     *
     * @param self The {@code this} reference for the {@link UpdateRequest}.
     * @param params The uncached script parameters
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static UpdateRequest scriptParams(UpdateRequest self, Closure params) throws Exception {
        self.scriptParams(params.asMap())
    }

    /**
     * Sets the content {@code source} to update.
     *
     * @param self The {@code this} reference for the {@link UpdateRequestBuilder}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static UpdateRequestBuilder setSource(UpdateRequestBuilder self, Closure source) throws Exception {
        self.setSource(source.asJsonBytes())
    }
}