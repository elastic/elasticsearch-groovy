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
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryResponse
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.client.ClusterAdminClient
import org.elasticsearch.snapshots.SnapshotState
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
    void testHealthRequestSync() {
        indexDoc(indexName, typeName) { name = "ignored" }

        ClusterHealthResponse response = clusterAdminClient.healthSync {
            indices indexName
            waitForStatus ClusterHealthStatus.YELLOW
        }

        // waited for Yellow, so it had better not be Red
        assert response.status != ClusterHealthStatus.RED
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

    @Test
    void testHealthRequestAsync() {
        indexDoc(indexName, typeName) { name = "ignored" }

        ClusterHealthResponse response = clusterAdminClient.healthAsync {
            indices indexName
            waitForStatus ClusterHealthStatus.YELLOW
        }.actionGet()

        // waited for Yellow, so it had better not be Red
        assert response.status != ClusterHealthStatus.RED
    }

    @Test
    void testPutRepositoryRequestAndGetRepositoryRequestSync() {
        String repoName = "test-repo"
        String absolutePath = randomRepoPath().toAbsolutePath()

        // Create the repository
        PutRepositoryResponse response = clusterAdminClient.putRepositorySync {
            name repoName
            type "fs"
            settings {
                compress = true
                location = absolutePath
            }
        }

        assert response.acknowledged

        // verify that it exists
        GetRepositoriesResponse getResponse = clusterAdminClient.getRepositoriesSync {
            repositories repoName
        }

        assert getResponse.repositories()[0].name() == repoName
        assert getResponse.repositories()[0].settings().get("location") == absolutePath
    }

    @Test
    void testPutRepositoryRequestAndGetRepositoryRequest() {
        String repoName = "test-repo-async"
        String absolutePath = randomRepoPath().toAbsolutePath()

        // Create the repository
        PutRepositoryResponse response = clusterAdminClient.putRepository {
            name repoName
            type "fs"
            settings {
                compress = true
                location = absolutePath
            }
        }.actionGet()

        assert response.acknowledged

        // verify that it exists
        GetRepositoriesResponse getResponse = clusterAdminClient.getRepositories {
            repositories repoName
        }.actionGet()

        assert getResponse.repositories()[0].name() == repoName
        assert getResponse.repositories()[0].settings().get("location") == absolutePath
    }

    @Test
    void testPutRepositoryRequestAndGetRepositoryRequestAsync() {
        String repoName = "test-repo-async"
        String absolutePath = randomRepoPath().toAbsolutePath()

        // Create the repository
        PutRepositoryResponse response = clusterAdminClient.putRepositoryAsync {
            name repoName
            type "fs"
            settings {
                compress = true
                location = absolutePath
            }
        }.actionGet()

        assert response.acknowledged

        // verify that it exists
        GetRepositoriesResponse getResponse = clusterAdminClient.getRepositoriesAsync {
            repositories repoName
        }.actionGet()

        assert getResponse.repositories()[0].name() == repoName
        assert getResponse.repositories()[0].settings().get("location") == absolutePath
    }

    @Test
    void testCreateSnapshotRequestSync() {
        String repoName = "test-create-snapshot-repo"
        String snapshotName = "test-create-snapshot"
        String absolutePath = randomRepoPath().toAbsolutePath()

        // Write a document
        indexDoc(indexName, typeName) { value = "ignored" }
        // flush the index to disk
        client.admin.indices.flushSync { indices indexName }

        // Create the repository
        PutRepositoryResponse putResponse = clusterAdminClient.putRepositorySync {
            name repoName
            type "fs"
            settings {
                location = absolutePath
            }
        }

        // sanity check
        assert putResponse.acknowledged

        // Create the snapshot
        CreateSnapshotResponse response = clusterAdminClient.createSnapshotSync {
            repository repoName
            snapshot snapshotName
            indices indexName
            waitForCompletion true
        }

        assert response.snapshotInfo.name() == snapshotName
        assert response.snapshotInfo.state() == SnapshotState.SUCCESS
        assert response.snapshotInfo.indices()[0] == indexName
    }

    @Test
    void testCreateSnapshotRequest() {
        String repoName = "test-create-snapshot-repo-async"
        String snapshotName = "test-create-snapshot-async"
        String absolutePath = randomRepoPath().toAbsolutePath()

        // Write a document
        indexDoc(indexName, typeName) { value = "ignored" }
        // flush the index to disk
        client.admin.indices.flush { indices indexName }.actionGet()

        // Create the repository
        PutRepositoryResponse putResponse = clusterAdminClient.putRepository {
            name repoName
            type "fs"
            settings {
                location = absolutePath
            }
        }.actionGet()

        // sanity check
        assert putResponse.acknowledged

        // Create the snapshot
        CreateSnapshotResponse response = clusterAdminClient.createSnapshot {
            repository repoName
            snapshot snapshotName
            indices indexName
            waitForCompletion true
        }.actionGet()

        assert response.snapshotInfo.name() == snapshotName
        assert response.snapshotInfo.state() == SnapshotState.SUCCESS
        assert response.snapshotInfo.indices()[0] == indexName
    }

    @Test
    void testCreateSnapshotRequestAsync() {
        String repoName = "test-create-snapshot-repo-async"
        String snapshotName = "test-create-snapshot-async"
        String absolutePath = randomRepoPath().toAbsolutePath()

        // Write a document
        indexDoc(indexName, typeName) { value = "ignored" }
        // flush the index to disk
        client.admin.indices.flushAsync { indices indexName }.actionGet()

        // Create the repository
        PutRepositoryResponse putResponse = clusterAdminClient.putRepositoryAsync {
            name repoName
            type "fs"
            settings {
                location = absolutePath
            }
        }.actionGet()

        // sanity check
        assert putResponse.acknowledged

        // Create the snapshot
        CreateSnapshotResponse response = clusterAdminClient.createSnapshotAsync {
            repository repoName
            snapshot snapshotName
            indices indexName
            waitForCompletion true
        }.actionGet()

        assert response.snapshotInfo.name() == snapshotName
        assert response.snapshotInfo.state() == SnapshotState.SUCCESS
        assert response.snapshotInfo.indices()[0] == indexName
    }

    @Test
    void testRestoreSnapshotRequestSync() {
        String repoName = "test-restore-snapshot-repo"
        String snapshotName = "test-restore-snapshot"
        String absolutePath = randomRepoPath().toAbsolutePath()
        String restoredIndexName = indexName + "-restored"
        String expectedValue = "expected"

        // Write a document
        String docId = indexDoc(indexName, typeName) { value = expectedValue }

        // Create the repository
        PutRepositoryResponse putResponse = clusterAdminClient.putRepositorySync {
            name repoName
            type "fs"
            settings {
                location = absolutePath
            }
        }

        // sanity check
        assert putResponse.acknowledged

        // Create the snapshot
        CreateSnapshotResponse createResponse = clusterAdminClient.createSnapshotSync {
            repository repoName
            snapshot snapshotName
            indices indexName
            waitForCompletion true
        }

        // sanity check
        assert createResponse.snapshotInfo.state() == SnapshotState.SUCCESS

        // Restore the snapshot to another index (indexName -> restoredIndexName)
        RestoreSnapshotResponse response = clusterAdminClient.restoreSnapshotSync {
            repository repoName
            snapshot snapshotName
            renamePattern indexName
            renameReplacement restoredIndexName
            waitForCompletion true
        }

        // ensure that we appropriately restored from the snapshot
        assert response.restoreInfo.name() == snapshotName
        assert response.restoreInfo.failedShards() == 0
        assert response.restoreInfo.indices()[0] == restoredIndexName

        ClusterHealthResponse healthResponse = clusterAdminClient.healthSync {
            indices restoredIndexName
            waitForStatus ClusterHealthStatus.YELLOW
        }

        // sanity check
        assert ! healthResponse.timedOut

        // Ensure that the restored index was expected renamed
        GetResponse getResponse = client.getSync {
            index restoredIndexName
            type typeName
            id docId
        }

        assert getResponse.exists
        assert getResponse.sourceAsMap.value == expectedValue
    }

    @Test
    void testRestoreSnapshotRequest() {
        String repoName = "test-restore-snapshot-repo-async"
        String snapshotName = "test-restore-snapshot-async"
        String absolutePath = randomRepoPath().toAbsolutePath()
        String restoredIndexName = indexName + "-restored-async"
        String expectedValue = "expected"

        // Write a document
        String docId = indexDoc(indexName, typeName) { value = expectedValue }

        // Create the repository
        PutRepositoryResponse putResponse = clusterAdminClient.putRepository {
            name repoName
            type "fs"
            settings {
                location = absolutePath
            }
        }.actionGet()

        // sanity check
        assert putResponse.acknowledged

        // Create the snapshot
        CreateSnapshotResponse createResponse = clusterAdminClient.createSnapshot {
            repository repoName
            snapshot snapshotName
            indices indexName
            waitForCompletion true
        }.actionGet()

        // sanity check
        assert createResponse.snapshotInfo.state() == SnapshotState.SUCCESS

        // Restore the snapshot to another index (indexName -> restoredIndexName)
        RestoreSnapshotResponse response = clusterAdminClient.restoreSnapshot {
            repository repoName
            snapshot snapshotName
            renamePattern indexName
            renameReplacement restoredIndexName
            waitForCompletion true
        }.actionGet()

        // ensure that we appropriately restored from the snapshot
        assert response.restoreInfo.name() == snapshotName
        assert response.restoreInfo.failedShards() == 0
        assert response.restoreInfo.indices()[0] == restoredIndexName

        ClusterHealthResponse healthResponse = clusterAdminClient.health {
            indices restoredIndexName
            waitForStatus ClusterHealthStatus.YELLOW
        }.actionGet()

        // sanity check
        assert ! healthResponse.timedOut

        // Ensure that the restored index was expected renamed
        GetResponse getResponse = client.get {
            index restoredIndexName
            type typeName
            id docId
        }.actionGet()

        assert getResponse.exists
        assert getResponse.sourceAsMap.value == expectedValue
    }

    @Test
    void testRestoreSnapshotRequestAsync() {
        String repoName = "test-restore-snapshot-repo-async"
        String snapshotName = "test-restore-snapshot-async"
        String absolutePath = randomRepoPath().toAbsolutePath()
        String restoredIndexName = indexName + "-restored-async"
        String expectedValue = "expected"

        // Write a document
        String docId = indexDoc(indexName, typeName) { value = expectedValue }

        // Create the repository
        PutRepositoryResponse putResponse = clusterAdminClient.putRepositoryAsync {
            name repoName
            type "fs"
            settings {
                location = absolutePath
            }
        }.actionGet()

        // sanity check
        assert putResponse.acknowledged

        // Create the snapshot
        CreateSnapshotResponse createResponse = clusterAdminClient.createSnapshotAsync {
            repository repoName
            snapshot snapshotName
            indices indexName
            waitForCompletion true
        }.actionGet()

        // sanity check
        assert createResponse.snapshotInfo.state() == SnapshotState.SUCCESS

        // Restore the snapshot to another index (indexName -> restoredIndexName)
        RestoreSnapshotResponse response = clusterAdminClient.restoreSnapshotAsync {
            repository repoName
            snapshot snapshotName
            renamePattern indexName
            renameReplacement restoredIndexName
            waitForCompletion true
        }.actionGet()

        // ensure that we appropriately restored from the snapshot
        assert response.restoreInfo.name() == snapshotName
        assert response.restoreInfo.failedShards() == 0
        assert response.restoreInfo.indices()[0] == restoredIndexName

        ClusterHealthResponse healthResponse = clusterAdminClient.healthAsync {
            indices restoredIndexName
            waitForStatus ClusterHealthStatus.YELLOW
        }.actionGet()

        // sanity check
        assert ! healthResponse.timedOut

        // Ensure that the restored index was expected renamed
        GetResponse getResponse = client.getAsync {
            index restoredIndexName
            type typeName
            id docId
        }.actionGet()

        assert getResponse.exists
        assert getResponse.sourceAsMap.value == expectedValue
    }
}
