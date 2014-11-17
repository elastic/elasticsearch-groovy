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
package org.elasticsearch.groovy.action.admin.cluster.settings

import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequest
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequestBuilder
import org.elasticsearch.client.ClusterAdminClient

/**
 * {@code ClusterUpdateSettingsRequestExtensions} provides Groovy-friendly {@link ClusterUpdateSettingsRequest}
 * extensions.
 * @see ClusterAdminClient
 */
class ClusterUpdateSettingsRequestExtensions {
    /**
     * Updates the persistent {@code settings} for the entire cluster.
     *
     * @param self The {@code this} reference for the {@link ClusterUpdateSettingsRequest}.
     * @param settings The persistent settings
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ClusterUpdateSettingsRequest persistentSettings(ClusterUpdateSettingsRequest self, Closure settings) {
        self.persistentSettings(settings.asMap())
    }

    /**
     * Updates the transient {@code settings} for the entire cluster.
     *
     * @param self The {@code this} reference for the {@link ClusterUpdateSettingsRequest}.
     * @param settings The transient settings
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ClusterUpdateSettingsRequest transientSettings(ClusterUpdateSettingsRequest self, Closure settings) {
        self.transientSettings(settings.asMap())
    }

    /**
     * Updates the persistent {@code settings} for the entire cluster.
     *
     * @param self The {@code this} reference for the {@link ClusterUpdateSettingsRequestBuilder}.
     * @param source The persistent settings
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ClusterUpdateSettingsRequestBuilder setPersistentSettings(ClusterUpdateSettingsRequestBuilder self,
                                                                     Closure settings) {
        self.setPersistentSettings(settings.asMap())
    }

    /**
     * Updates the transient {@code settings} for the entire cluster.
     *
     * @param self The {@code this} reference for the {@link ClusterUpdateSettingsRequestBuilder}.
     * @param source The transient settings
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ClusterUpdateSettingsRequestBuilder setTransientSettings(ClusterUpdateSettingsRequestBuilder self,
                                                                    Closure settings) {
        self.setTransientSettings(settings.asMap())
    }
}