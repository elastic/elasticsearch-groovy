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
package org.elasticsearch.groovy.client.action.admin.indices.create

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder
import org.elasticsearch.client.IndicesAdminClient

/**
 * {@code CreateIndexRequestExtensions} provides Groovy-friendly {@link CreateIndexRequest} extensions.
 * @see IndicesAdminClient#create(CreateIndexRequest)
 */
class CreateIndexRequestExtensions {
    /**
     * Sets the index settings and mappings as a single {@code source}.
     *
     * @param self The {@code this} reference for the {@link CreateIndexRequest}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static CreateIndexRequest source(CreateIndexRequest self, Closure source) {
        self.source(source.asJsonBytes())
    }

    /**
     * Sets the index settings and mappings as a single {@code source}.
     *
     * @param self The {@code this} reference for the {@link CreateIndexRequest}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static CreateIndexRequest setSource(CreateIndexRequest self, Closure source) {
        source(self, source)
    }

    /**
     * Sets the index {@code settings}.
     *
     * @param self The {@code this} reference for the {@link CreateIndexRequest}.
     * @param settings The index settings
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static CreateIndexRequest settings(CreateIndexRequest self, Closure settings) {
        self.settings(settings.asJsonString())
    }

    /**
     * Sets the index {@code settings}.
     *
     * @param self The {@code this} reference for the {@link CreateIndexRequest}.
     * @param settings The index settings
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static CreateIndexRequest setSettings(CreateIndexRequest self, Closure settings) {
        settings(self, settings)
    }

    /**
     * Sets the index {@code type} and its {@code mapping}.
     *
     * @param self The {@code this} reference for the {@link CreateIndexRequest}.
     * @param type The type to create the {@code mapping}
     * @param mapping The {@code type} mapping
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null} except {@code type}
     */
    static CreateIndexRequest mapping(CreateIndexRequest self, String type, Closure mapping) {
        self.mapping(type, mapping.asJsonString())
    }

    /**
     * Sets the index {@code type} and its {@code mapping}.
     *
     * @param self The {@code this} reference for the {@link CreateIndexRequest}.
     * @param type The type to create the {@code mapping}
     * @param mapping The {@code type} mapping
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static CreateIndexRequest setMapping(CreateIndexRequest self, String type, Closure mapping) {
        mapping(self, type, mapping)
    }

    /**
     * Sets the index settings and mappings as a single {@code source}.
     *
     * @param self The {@code this} reference for the {@link CreateIndexRequestBuilder}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static CreateIndexRequestBuilder setSource(CreateIndexRequestBuilder self, Closure source) {
        self.setSource(source.asJsonBytes())
    }

    /**
     * Sets the index {@code settings}.
     *
     * @param self The {@code this} reference for the {@link CreateIndexRequestBuilder}.
     * @param settings The index settings
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static CreateIndexRequestBuilder setSettings(CreateIndexRequestBuilder self, Closure settings) {
        self.setSettings(settings.asJsonString())
    }

    /**
     * Adds the index {@code type} and its {@code mapping}.
     *
     * @param self The {@code this} reference for the {@link CreateIndexRequestBuilder}.
     * @param type The type to create the {@code mapping}
     * @param mapping The {@code type} mapping
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static CreateIndexRequestBuilder addMapping(CreateIndexRequestBuilder self, String type, Closure mapping) {
        self.addMapping(type, mapping.asJsonString())
    }
}