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
package org.elasticsearch.groovy.common.settings

import org.elasticsearch.ElasticsearchIllegalArgumentException
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.settings.loader.JsonSettingsLoader

/**
 * {@code ImmutableSettingsBuilderExtensions} provide Groovy-friendly extensions to {@link ImmutableSettings.Builder}.
 * <p />
 * In particular, this adds the ability to specify settings in the form of a {@link Closure} in addition to existing
 * options.
 */
class ImmutableSettingsBuilderExtensions {
    /**
     * Explicit settings to set.
     * <pre>
     * ImmutableSettings.Builder.settingsBuilder().put {
     *     node {
     *         client = true
     *     }
     *     cluster {
     *         name = 'es-cluster-name'
     *     }
     * }
     * </pre>
     * Note: This provides an advantage over the {@code Map} variant that requires a string-to-string mapping. This
     * will in effect create a JSON map out of the {@code settings} and then convert that to a string-to-string mapping
     * for you.
     *
     * @param self The {@code this} reference for the {@link ImmutableSettings.Builder}
     * @param settings The settings specified as a {@link Closure}
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     * @throws ElasticsearchIllegalArgumentException if the {@code settings} fail to parse as JSON
     */
    static ImmutableSettings.Builder put(ImmutableSettings.Builder self, Closure settings) {
        try {
            self.put(new JsonSettingsLoader().load(settings.asJsonBytes()))
        }
        catch (IOException e) {
            throw new ElasticsearchIllegalArgumentException("Closure failed to map to valid JSON.", e)
        }

        self
    }
}
