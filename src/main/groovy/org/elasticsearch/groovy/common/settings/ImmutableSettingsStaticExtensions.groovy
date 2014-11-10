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

/**
 * {@code ImmutableSettingsStaticExtensions} provide {@code static}, Groovy-friendly extensions to
 * {@link ImmutableSettings}.
 * <p />
 * In particular, this adds the ability to specify settings in the form of a {@link Closure} when creating a
 * new {@link ImmutableSettings#settingsBuilder() Builder}.
 */
class ImmutableSettingsStaticExtensions {
    /**
     * Explicit settings to set.
     * <pre>
     * ImmutableSettings.settingsBuilder {
     *     node {
     *         client = true
     *     }
     *     cluster {
     *         name = 'es-cluster-name'
     *     }
     * }.build()
     * </pre>
     *
     * @param selfType The {@code static} type that this method is added too (unused)
     * @param settings The settings specified as a {@link Closure}
     * @return Always a new {@link ImmutableSettings.Builder} with the {@code settings} applied to it
     * @throws NullPointerException if {@code settings} is {@code null}
     * @throws ElasticsearchIllegalArgumentException if the {@code settings} fail to parse as JSON
     */
    static ImmutableSettings.Builder settingsBuilder(ImmutableSettings selfType, Closure settings) {
        ImmutableSettings.settingsBuilder().put(settings)
    }
}
