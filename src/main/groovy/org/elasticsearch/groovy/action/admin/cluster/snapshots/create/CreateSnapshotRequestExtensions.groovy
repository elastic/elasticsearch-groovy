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
package org.elasticsearch.groovy.action.admin.cluster.snapshots.create

import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequest
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequestBuilder
import org.elasticsearch.client.ClusterAdminClient

/**
 * {@code CreateSnapshotRequestExtensions} provides Groovy-friendly {@link CreateSnapshotRequest} extensions.
 * <p>
 * Note: This extension intentionally does _not_ provide a {@code Closure} overload of
 * {@link CreateSnapshotRequest#source(java.util.Map)} because it is interpreted the same as using
 * {@link org.codehaus.groovy.runtime.DefaultGroovyMethods#with} and a {@code Closure}.
 * @see ClusterAdminClient
 */
class CreateSnapshotRequestExtensions {
    /**
     * Sets the {@code settings} for the snapshot.
     * <pre>
     * CreateSnapshotResponse response = client.admin.cluster.createSnapshot {
     *   repository "snapshot_repo"
     *   snapshot "snapshot1"
     *   indices "my-index", "my-other-index"
     *   settings {
     *      // ...
     *   }
     * }.actionGet()
     * </pre>
     *
     * @param self The {@code this} reference for the {@link CreateSnapshotRequest}.
     * @param settings The snapshot settings
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static CreateSnapshotRequest settings(CreateSnapshotRequest self, Closure settings) {
        self.settings(settings.asMap())
    }

    /**
     * Sets the {@code settings} for the snapshot.
     *
     * @param self The {@code this} reference for the {@link CreateSnapshotRequestBuilder}.
     * @param source The snapshot settings
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static CreateSnapshotRequestBuilder setSettings(CreateSnapshotRequestBuilder self, Closure settings) {
        self.setSettings(settings.asMap())
    }
}