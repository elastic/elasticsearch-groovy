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

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus
import org.elasticsearch.client.ClusterAdminClient
import org.elasticsearch.test.ElasticsearchIntegrationTest.ClusterScope
import org.elasticsearch.test.ElasticsearchIntegrationTest.Scope

import org.junit.Before
import org.junit.Test

/**
 * Tests {@code ActionRequest}s added by {@link ClusterAdminClientExtensions}.
 */
@ClusterScope(scope = Scope.TEST)
class ClusterAdminClientExtensionsActionTests extends AbstractClientTests {
    /**
     * The index to use for most tests.
     */
    String indexName = 'cluster'
    /**
     * The index type to use for most tests.
     */
    String typeName = 'actions'

    /**
     * Enhanced {@link ClusterAdminClient} that is tested alongside relevant {@link #client} actions.
     */
    ClusterAdminClient clusterAdminClient

    @Before
    void setupAdmin() {
        clusterAdminClient = client.admin.cluster
    }

    @Test
    void testHealthRequest() {
        indexDoc(indexName, typeName) { name = "ignored" }

        ClusterHealthResponse response = clusterAdminClient.health {
            indices indexName
            waitForStatus ClusterHealthStatus.YELLOW
        }.actionGet()

        // waited for Yellow, so it had better not be Red
        assert response.status != ClusterHealthStatus.RED
    }
}