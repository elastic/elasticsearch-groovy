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
package org.elasticsearch.groovy.node

import org.elasticsearch.ElasticsearchIllegalArgumentException
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.settings.loader.JsonSettingsLoader
import org.elasticsearch.groovy.common.xcontent.GXContentBuilder
import org.elasticsearch.node.NodeBuilder

/**
 * {@code NodeBuilderExtensions} provides convenience methods to standard Elasticsearch {@link NodeBuilder}s to make
 * them more Groovy friendly.
 */
class NodeBuilderExtensions {
    /**
     * Set any addition settings by working directly against the internally used settings builder.
     *
     * @param self The {@code this} reference for the {@link NodeBuilder}
     * @return Always {@link NodeBuilder#settings()}.
     * @throws NullPointerException if {@code self} is {@code null}
     */
    static ImmutableSettings.Builder getSettings(NodeBuilder self) {
        self.settings()
    }

    /**
     * Explicit node settings to set.
     * <p />
     * <pre>nodeBuilder().settings {
     *     node {
     *         local = true
     *     }
     *     cluster {
     *         name = 'es-cluster-name'
     *     }
     * }</pre>
     *
     * @param self The {@code this} reference for the {@link NodeBuilder}
     * @param settings The settings specified as a {@link Closure}
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     * @throws ElasticsearchIllegalArgumentException if the {@code settings} fail to parse as JSON
     */
    static NodeBuilder settings(NodeBuilder self, Closure settings) {
        byte[] settingsBytes = new GXContentBuilder().buildAsBytes(settings)

        try {
            self.settings().put(new JsonSettingsLoader().load(settingsBytes))
        }
        catch (IOException e) {
            throw new ElasticsearchIllegalArgumentException("Closure fail to map to JSON.", e)
        }

        self
    }
}