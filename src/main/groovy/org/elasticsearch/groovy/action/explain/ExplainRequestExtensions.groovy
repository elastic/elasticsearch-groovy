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
package org.elasticsearch.groovy.action.explain

import org.elasticsearch.action.explain.ExplainRequest
import org.elasticsearch.action.explain.ExplainRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.client.Requests

/**
 * {@code ExplainRequestExtensions} provides Groovy-friendly {@link ExplainRequest} extensions.
 * @see Client#explain(ExplainRequest)
 */
class ExplainRequestExtensions {
    /**
     * Sets the content query {@code source} to explain.
     *
     * @param self The {@code this} reference for the {@link ExplainRequest}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ExplainRequest source(ExplainRequest self, Closure source) {
        self.source(source.build(Requests.CONTENT_TYPE).bytes())
    }

    /**
     * Sets the content query {@code source} to explain.
     *
     * @param self The {@code this} reference for the {@link ExplainRequestBuilder}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ExplainRequestBuilder setSource(ExplainRequestBuilder self, Closure source) {
        self.setSource(source.build(Requests.CONTENT_TYPE).bytes())
    }
}