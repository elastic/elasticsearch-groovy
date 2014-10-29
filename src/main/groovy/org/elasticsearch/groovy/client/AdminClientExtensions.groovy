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
package org.elasticsearch.groovy.client

import org.elasticsearch.client.AdminClient
import org.elasticsearch.client.Client
import org.elasticsearch.client.ClusterAdminClient
import org.elasticsearch.client.IndicesAdminClient

/**
 * {@code AdminClientExtensions} provides Groovy-friendly access to {@link AdminClient}s.
 * @see Client#admin()
 */
class AdminClientExtensions {

    /**
     * Get the {@link ClusterAdminClient} used to perform actions and operations against the cluster.
     *
     * @param self The {@code this} reference for the {@link AdminClientExtensions}.
     * @return Always {@link AdminClient#cluster()}.
     */
    static ClusterAdminClient getCluster(AdminClient self) {
        self.cluster()
    }

    /**
     * Get the {@link IndicesAdminClient} used to perform actions and operations against the cluster.
     *
     * @param self The {@code this} reference for the {@link AdminClientExtensions}.
     * @return Always {@link AdminClient#indices()}.
     */
    static IndicesAdminClient getIndices(AdminClient self) {
        self.indices()
    }
}