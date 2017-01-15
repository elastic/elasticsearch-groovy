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

import org.elasticsearch.action.DocWriteResponse
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse
import org.elasticsearch.action.admin.cluster.node.hotthreads.NodesHotThreadsResponse
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse
import org.elasticsearch.action.admin.cluster.repositories.delete.DeleteRepositoryResponse
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryResponse
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.status.SnapshotsStatusResponse
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse
import org.elasticsearch.action.admin.cluster.storedscripts.DeleteStoredScriptResponse
import org.elasticsearch.action.admin.cluster.storedscripts.GetStoredScriptResponse
import org.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptResponse
import org.elasticsearch.action.admin.cluster.tasks.PendingClusterTasksResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.client.ClusterAdminClient
import org.elasticsearch.cluster.SnapshotsInProgress
import org.elasticsearch.snapshots.SnapshotState
import org.elasticsearch.test.ESIntegTestCase.ClusterScope
import org.elasticsearch.test.ESIntegTestCase.Scope

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
    void testPutRepositoryRequestSync() {
        String repoName = "test-repo-sync"
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
    }

    @Test
    void testPutRepositoryRequest() {
        String repoName = "test-repo"
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
    }

    @Test
    void testPutRepositoryRequestAsync() {
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
    }

    @Test
    void testGetRepositoryRequestSync() {
        String repoName = "test-get-repo-sync"
        // Create the repository
        String absolutePath = createRepository(repoName)

        // verify that it exists
        GetRepositoriesResponse response = clusterAdminClient.getRepositoriesSync {
            repositories repoName
        }

        assert response.repositories()[0].name() == repoName
        assert response.repositories()[0].settings().get("location") == absolutePath
    }

    @Test
    void testGetRepositoryRequest() {
        String repoName = "test-get-repo"
        // Create the repository
        String absolutePath = createRepository(repoName)

        // verify that it exists
        GetRepositoriesResponse response = clusterAdminClient.getRepositories {
            repositories repoName
        }.actionGet()

        assert response.repositories()[0].name() == repoName
        assert response.repositories()[0].settings().get("location") == absolutePath
    }

    @Test
    void testGetRepositoryRequestAsync() {
        String repoName = "test-get-repo-async"
        // Create the repository
        String absolutePath = createRepository(repoName)

        // verify that it exists
        GetRepositoriesResponse response = clusterAdminClient.getRepositoriesAsync {
            repositories repoName
        }.actionGet()

        assert response.repositories()[0].name() == repoName
        assert response.repositories()[0].settings().get("location") == absolutePath
    }

    @Test
    void testDeleteRepositoryRequestRequestSync() {
        String repoName = "test-delete-repo-sync"

        // Create the repository
        createRepository(repoName)

        // verify that it exists
        DeleteRepositoryResponse response = clusterAdminClient.deleteRepositorySync {
            name repoName
        }

        // sanity check
        assert response.acknowledged
    }

    @Test
    void testDeleteRepositoryRequestRequest() {
        String repoName = "test-delete-repo"

        // Create the repository
        createRepository(repoName)

        // verify that it exists
        DeleteRepositoryResponse response = clusterAdminClient.deleteRepository {
            name repoName
        }.actionGet()

        // sanity check
        assert response.acknowledged
    }

    @Test
    void testDeleteRepositoryRequestAsync() {
        String repoName = "test-delete-repo-async"

        // Create the repository
        createRepository(repoName)

        // verify that it exists
        DeleteRepositoryResponse response = clusterAdminClient.deleteRepositoryAsync {
            name repoName
        }.actionGet()

        // sanity check
        assert response.acknowledged
    }

    @Test
    void testCreateSnapshotRequestSync() {
        String repoName = "test-create-snapshot-repo-sync"
        String snapshotName = "test-create-snapshot-sync"

        // Create the repository
        createRepository(repoName)

        // Write a document
        indexDoc(indexName, typeName) { value = "ignored" }
        // flush the index to disk
        client.admin.indices.flushSync { indices indexName }

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
        String repoName = "test-create-snapshot-repo"
        String snapshotName = "test-create-snapshot"

        // Create the repository
        createRepository(repoName)

        // Write a document
        indexDoc(indexName, typeName) { value = "ignored" }
        // flush the index to disk
        client.admin.indices.flush { indices indexName }.actionGet()

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

        // Create the repository
        createRepository(repoName)

        // Write a document
        indexDoc(indexName, typeName) { value = "ignored" }
        // flush the index to disk
        client.admin.indices.flushAsync { indices indexName }.actionGet()

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
    void testGetSnapshotsRequestSync() {
        String repoName = "test-get-snapshots-repo-sync"
        String snapshotName = "test-get-snapshots-sync"

        createRepositoryAndSnapshot(repoName, snapshotName)

        // Check for the snapshot
        GetSnapshotsResponse response = clusterAdminClient.getSnapshotsSync {
            repository repoName
        }

        // verify that it exists
        assert response.snapshots[0].name() == snapshotName
    }

    @Test
    void testGetSnapshotsRequest() {
        String repoName = "test-get-snapshots-repo"
        String snapshotName = "test-get-snapshots"

        createRepositoryAndSnapshot(repoName, snapshotName)

        // Check for the snapshot
        GetSnapshotsResponse response = clusterAdminClient.getSnapshots {
            repository repoName
        }.actionGet()

        // verify that it exists
        assert response.snapshots[0].name() == snapshotName
    }

    @Test
    void testGetSnapshotsRequestAsync() {
        String repoName = "test-get-snapshots-repo-async"
        String snapshotName = "test-get-snapshots-async"

        createRepositoryAndSnapshot(repoName, snapshotName)

        // Check for the snapshot
        GetSnapshotsResponse response = clusterAdminClient.getSnapshotsAsync {
            repository repoName
        }.actionGet()

        // verify that it exists
        assert response.snapshots[0].name() == snapshotName
    }

    @Test
    void testSnapshotsStatusRequestSync() {
        String repoName = "test-snapshots-status-repo-sync"
        String snapshotName = "test-restore-snapshot-sync"

        createRepositoryAndSnapshot(repoName, snapshotName)

        // Check for the snapshot's status
        SnapshotsStatusResponse response = clusterAdminClient.snapshotsStatusSync {
            repository repoName
            snapshots snapshotName
        }

        // verify that we found it and that it's still valid
        assert response.snapshots[0].state == SnapshotsInProgress.State.SUCCESS
    }

    @Test
    void testSnapshotsStatusRequest() {
        String repoName = "test-snapshots-status-repo"
        String snapshotName = "test-restore-snapshot"

        createRepositoryAndSnapshot(repoName, snapshotName)

        // Check for the snapshot's status
        SnapshotsStatusResponse response = clusterAdminClient.snapshotsStatus {
            repository repoName
            snapshots snapshotName
        }.actionGet()

        // verify that we found it and that it's still valid
        assert response.snapshots[0].state == SnapshotsInProgress.State.SUCCESS
    }

    @Test
    void testSnapshotsStatusRequestAsync() {
        String repoName = "test-snapshots-status-repo-async"
        String snapshotName = "test-restore-snapshot-async"

        createRepositoryAndSnapshot(repoName, snapshotName)

        // Check for the snapshot's status
        SnapshotsStatusResponse response = clusterAdminClient.snapshotsStatusAsync {
            repository repoName
            snapshots snapshotName
        }.actionGet()

        // verify that we found it and that it's still valid
        assert response.snapshots[0].state == SnapshotsInProgress.State.SUCCESS
    }

    @Test
    void testRestoreSnapshotRequestSync() {
        String repoName = "test-restore-snapshot-repo-sync"
        String snapshotName = "test-restore-snapshot-sync"
        String restoredIndexName = indexName + "-restored-sync"
        String expectedValue = "expected"

        String docId = createRepositoryAndSnapshot(repoName, snapshotName, expectedValue)

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
        String repoName = "test-restore-snapshot-repo"
        String snapshotName = "test-restore-snapshot"
        String restoredIndexName = indexName + "-restored"
        String expectedValue = "expected"

        String docId = createRepositoryAndSnapshot(repoName, snapshotName, expectedValue)

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
        String restoredIndexName = indexName + "-restored-async"
        String expectedValue = "expected"

        String docId = createRepositoryAndSnapshot(repoName, snapshotName, expectedValue)

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

    @Test
    void testDeleteSnapshotRequestSync() {
        String repoName = "test-delete-snapshot-repo-sync"
        String snapshotName = "test-delete-snapshot-sync"

        createRepositoryAndSnapshot(repoName, snapshotName)

        // Delete the snapshot
        DeleteSnapshotResponse response = clusterAdminClient.deleteSnapshotSync {
            repository repoName
            snapshot snapshotName
        }

        // sanity check
        assert response.acknowledged
    }

    @Test
    void testDeleteSnapshotRequest() {
        String repoName = "test-delete-snapshot-repo"
        String snapshotName = "test-delete-snapshot"

        createRepositoryAndSnapshot(repoName, snapshotName)

        // Delete the snapshot
        DeleteSnapshotResponse response = clusterAdminClient.deleteSnapshot {
            repository repoName
            snapshot snapshotName
        }.actionGet()

        // sanity check
        assert response.acknowledged
    }

    @Test
    void testDeleteSnapshotRequestAsync() {
        String repoName = "test-delete-snapshot-repo-async"
        String snapshotName = "test-delete-snapshot-async"

        createRepositoryAndSnapshot(repoName, snapshotName)

        // Delete the snapshot
        DeleteSnapshotResponse response = clusterAdminClient.deleteSnapshotAsync {
            repository repoName
            snapshot snapshotName
        }.actionGet()

        // sanity check
        assert response.acknowledged
    }

    @Test
    void testNodesHotThreadsRequestSync() {
        NodesHotThreadsResponse response = clusterAdminClient.nodesHotThreadsSync {
            // Currently defaults to 3, but we just want a quick test
            threads 1
        }

        // sanity check
        assert ! response.nodesMap.empty
    }

    @Test
    void testNodesHotThreadsRequest() {
        NodesHotThreadsResponse response = clusterAdminClient.nodesHotThreads {
            // Currently defaults to 3, but we just want a quick test
            threads 1
        }.actionGet()

        // sanity check
        assert ! response.nodesMap.empty
    }

    @Test
    void testNodesHotThreadsRequestAsync() {
        NodesHotThreadsResponse response = clusterAdminClient.nodesHotThreadsAsync {
            // Currently defaults to 3, but we just want a quick test
            threads 1
        }.actionGet()

        // sanity check
        assert ! response.nodesMap.empty
    }

    @Test
    void testPendingClusterTasksRequestSync() {
        PendingClusterTasksResponse response = clusterAdminClient.pendingClusterTasksSync {
            // nothing to pass in!
        }

        assert response.pendingTasks.size() == 0
    }

    @Test
    void testPendingClusterTasksRequest() {
        PendingClusterTasksResponse response = clusterAdminClient.pendingClusterTasks {
            // nothing to pass in!
        }.actionGet()

        assert response.pendingTasks.size() == 0
    }

    @Test
    void testPendingClusterTasksRequestAsync() {
        PendingClusterTasksResponse response = clusterAdminClient.pendingClusterTasksAsync {
            // nothing to pass in!
        }.actionGet()

        assert response.pendingTasks.size() == 0
    }

    @Test
    void testNodesInfoRequestSync() {
        NodesInfoResponse response = clusterAdminClient.nodesInfoSync {
            all() // collect all info possible
        }

        // sanity checks
        assert response.failures() == null
        response.nodes.each {
            // plugin info
            assert it.plugins != null
            // operating system info
            assert it.os != null
        }
    }

    @Test
    void testNodesInfoRequest() {
        NodesInfoResponse response = clusterAdminClient.nodesInfo {
            all() // collect all info possible
        }.actionGet()

        // sanity checks
        assert response.failures() == null
        response.nodes.each {
            // plugin info
            assert it.plugins != null
            // operating system info
            assert it.os != null
        }
    }

    @Test
    void testNodesInfoRequestAsync() {
        NodesInfoResponse response = clusterAdminClient.nodesInfoAsync {
            all() // collect all info possible
        }.actionGet()

        // sanity checks
        assert response.failures() == null
        response.nodes.each {
            // plugin info
            assert it.plugins != null
            // operating system info
            assert it.os != null
        }
    }

    @Test
    void testNodesStatsRequestSync() {
        NodesStatsResponse response = clusterAdminClient.nodesStatsSync {
            all() // collect all stats possible
        }

        // sanity checks
        assert response.failures() == null
        response.nodes.each {
            // file system info
            assert it.fs != null
            // operating system info
            assert it.os != null
        }
    }

    @Test
    void testNodesStatsRequest() {
        NodesStatsResponse response = clusterAdminClient.nodesStats {
            all() // collect all stats possible
        }.actionGet()

        // sanity checks
        assert response.failures() == null
        response.nodes.each {
            // file system info
            assert it.fs != null
            // operating system info
            assert it.os != null
        }
    }

    @Test
    void testNodesStatsRequestAsync() {
        NodesStatsResponse response = clusterAdminClient.nodesStatsAsync {
            all() // collect all stats possible
        }.actionGet()

        // sanity checks
        assert response.failures() == null
        response.nodes.each {
            // file system info
            assert it.fs != null
            // operating system info
            assert it.os != null
        }
    }

    @Test
    void testStateRequestSync() {
        ClusterStateResponse response = clusterAdminClient.stateSync {
            // we only need node info
            clear()
            nodes true
        }

        // sanity checks
        assert response.state != null
        assert response.state.nodes != null
    }

    @Test
    void testStateRequest() {
        ClusterStateResponse response = clusterAdminClient.state {
            // we only need node info
            clear()
            nodes true
        }.actionGet()

        // sanity checks
        assert response.state != null
        assert response.state.nodes != null
    }

    @Test
    void testStateRequestAsync() {
        ClusterStateResponse response = clusterAdminClient.stateAsync {
            // we only need node info
            clear()
            nodes true
        }.actionGet()

        // sanity checks
        assert response.state != null
        assert response.state.nodes != null
    }

    @Test
    void testPutStoredScriptRequestSync() {
        int startCount = randomInt(1024)

        String docId = indexDoc(indexName, typeName) {
            // actual value is ignored
            user = randomAsciiOfLengthBetween(1, 16)
            count = startCount
        }

        // index the script
        PutStoredScriptResponse response = clusterAdminClient.putStoredScriptSync {
            id 'testPutStoredScriptRequestSync'
            scriptLang 'painless'
            script {
                // NOTE: The script is [in this case] Painless, but it must be a string that is interpreted on the server
                script = "ctx._source.count += count"
            }
        }

        assert response.acknowledged

        UpdateResponse updateResponse = client.updateSync {
            index indexName
            type typeName
            id docId
            source {
                script_id 'testPutStoredScriptRequestSync'
                lang 'painless'
                params {
                    count = 5
                }
            }
        }

        assert updateResponse.result == DocWriteResponse.Result.UPDATED
        assert updateResponse.id == docId
        assert updateResponse.version == 2

        GetResponse getResponse = client.getSync {
            index indexName
            type typeName
            id docId
        }

        assert getResponse.exists
        assert getResponse.version == updateResponse.version
        assert getResponse.source.user != null
        assert getResponse.source.count == startCount + 5
    }

    @Test
    void testPutStoredScriptRequest() {
        int startCount = randomInt(1024)

        String docId = indexDoc(indexName, typeName) {
            // actual value is ignored
            user = randomAsciiOfLengthBetween(1, 16)
            count = startCount
        }

        // index the script
        PutStoredScriptResponse response = clusterAdminClient.putStoredScript {
            id 'testPutStoredScriptRequest'
            scriptLang 'painless'
            script {
                // NOTE: The script is [in this case] Painless, but it must be a string that is interpreted on the server
                script = "ctx._source.count += count"
            }
        }.actionGet()

        assert response.acknowledged

        UpdateResponse updateResponse = client.update {
            index indexName
            type typeName
            id docId
            source {
                script_id 'testPutStoredScriptRequest'
                lang 'painless'
                params {
                    count = 5
                }
            }
        }.actionGet()

        assert updateResponse.result == DocWriteResponse.Result.UPDATED
        assert updateResponse.id == docId
        assert updateResponse.version == 2

        GetResponse getResponse = client.get {
            index indexName
            type typeName
            id docId
        }.actionGet()

        assert getResponse.exists
        assert getResponse.version == updateResponse.version
        assert getResponse.source.user != null
        assert getResponse.source.count == startCount + 5
    }

    @Test
    void testPutStoredScriptRequestAsync() {
        int startCount = randomInt(1024)

        String docId = indexDoc(indexName, typeName) {
            // actual value is ignored
            user = randomAsciiOfLengthBetween(1, 16)
            count = startCount
        }

        // index the script
        PutStoredScriptResponse response = clusterAdminClient.putStoredScriptAsync {
            id 'testPutStoredScriptRequestAsync'
            scriptLang 'painless'
            script {
                // NOTE: The script is [in this case] Painless, but it must be a string that is interpreted on the server
                script = "ctx._source.count += count"
            }
        }.actionGet()

        assert response.acknowledged

        UpdateResponse updateResponse = client.updateAsync {
            index indexName
            type typeName
            id docId
            source {
                script_id 'testPutStoredScriptRequestAsync'
                lang 'groovy'
                params {
                    count = 5
                }
            }
        }.actionGet()

        assert updateResponse.result == DocWriteResponse.Result.UPDATED
        assert updateResponse.id == docId
        assert updateResponse.version == 2

        GetResponse getResponse = client.getAsync {
            index indexName
            type typeName
            id docId
        }.actionGet()

        assert getResponse.exists
        assert getResponse.version == updateResponse.version
        assert getResponse.source.user != null
        assert getResponse.source.count == startCount + 5
    }

    @Test
    void testGetStoredScriptRequestSync() {
        String painlessScript = "ctx._source.count += count"

        // index the script
        PutStoredScriptResponse putResponse = clusterAdminClient.putStoredScriptSync {
            id 'testGetStoredScriptRequestSync'
            scriptLang 'painless'
            script {
                script = painlessScript
            }
        }

        assert putResponse.acknowledged

        GetStoredScriptResponse response = clusterAdminClient.getStoredScriptSync {
            id 'testGetStoredScriptRequestSync'
            lang 'painless'
        }

        assert response.storedScript == painlessScript
    }

    @Test
    void testGetStoredScriptRequest() {
        String painlessScript = "ctx._source.count += count"

        // index the script
        PutStoredScriptResponse putResponse = clusterAdminClient.putStoredScript {
            id 'testGetStoredScriptRequest'
            scriptLang 'painless'
            script {
                script = painlessScript
            }
        }.actionGet()

        assert putResponse.acknowledged

        GetStoredScriptResponse response = clusterAdminClient.getStoredScript {
            id 'testGetStoredScriptRequest'
            lang 'painless'
        }.actionGet()

        assert response.storedScript == painlessScript
    }

    @Test
    void testGetStoredScriptRequestAsync() {
        String painlessScript = "ctx._source.count += count"

        // index the script
        PutStoredScriptResponse putResponse = clusterAdminClient.putStoredScriptAsync {
            id 'testGetStoredScriptRequest'
            scriptLang 'painless'
            script {
                script = painlessScript
            }
        }.actionGet()

        assert putResponse.created

        GetStoredScriptResponse response = clusterAdminClient.getStoredScriptAsync {
            id 'testGetIndexedScriptRequestAsync'
            lang 'painless'
        }.actionGet()

        assert response.storedScript == painlessScript
    }

    @Test
    void testDeleteStoredScriptRequestSync() {
        // index the script
        PutStoredScriptResponse putResponse = clusterAdminClient.putStoredScriptSync {
            id 'testDeleteStoredScriptRequestSync'
            scriptLang 'painless'
            script {
                script = "ctx._source.count += count"
            }
        }

        assert putResponse.acknowledged

        // perform the delete
        DeleteStoredScriptResponse response = clusterAdminClient.deleteStoredScriptSync {
            id 'testDeleteStoredScriptRequestSync'
            scriptLang 'painless'
        }

        assert response.acknowledged

        GetStoredScriptResponse getResponse = clusterAdminClient.getStoredScriptSync {
            id 'testDeleteStoredScriptRequestSync'
            lang 'painless'
        }

        assert getResponse.storedScript == null
    }

    @Test
    void testDeleteStoredScriptRequest() {
        // index the script
        PutStoredScriptResponse putResponse = clusterAdminClient.putStoredScript {
            id 'testDeleteStoredScriptRequest'
            scriptLang 'painless'
            script {
                script = "ctx._source.count += count"
            }
        }.actionGet()

        assert putResponse.acknowledged

        // perform the delete
        DeleteStoredScriptResponse response = clusterAdminClient.deleteStoredScript {
            id 'testDeleteStoredScriptRequest'
            scriptLang 'painless'
        }.actionGet()

        assert response.acknowledged

        GetStoredScriptResponse getResponse = clusterAdminClient.getStoredScript {
            id 'testDeleteStoredScriptRequest'
            lang 'painless'
        }.actionGet()

        assert getResponse.storedScript == null
    }

    @Test
    void testDeleteStoredScriptRequestAsync() {
        // index the script
        PutStoredScriptResponse putResponse = clusterAdminClient.putStoredScriptAsync {
            id 'testDeleteStoredScriptRequestAsync'
            scriptLang 'painless'
            script {
                script = "ctx._source.count += count"
            }
        }.actionGet()

        assert putResponse.acknowledged

        // perform the delete
        DeleteStoredScriptResponse response = clusterAdminClient.deleteStoredScriptAsync {
            id 'testDeleteStoredScriptRequestAsync'
            scriptLang 'painless'
        }.actionGet()

        assert response.acknowledged

        GetStoredScriptResponse getResponse = clusterAdminClient.getStoredScriptAsync {
            id 'testDeleteStoredScriptRequestAsync'
            lang 'painless'
        }.actionGet()

        assert getResponse.storedScript == null
    }


    /**
     * Create a repository with the {@code repoName} and a snapshot with the {@code snapshotName} within the repository.
     * <p>
     * This will:
     * <ol>
     * <li>Index a single document with a field named "value" whose value is defaulted to {@code "ignored"}.</li>
     * <li>Use the {@link #indexName} to snapshot and nothing else.</li>
     * <li>Wait until the snapshot is created before returning and it will assert that it has been created.</li>
     * </ol>
     *
     * @param repoName The name of the repository to create.
     * @param snapshotName The name of the snapshot to create.
     *
     * @see #createRepositoryAndSnapshot(String, String, String)
     */
    void createRepositoryAndSnapshot(String repoName, String snapshotName) {
        createRepositoryAndSnapshot(repoName, snapshotName, "ignored")
    }

    /**
     * Create a repository with the {@code repoName} and a snapshot with the {@code snapshotName} within the repository.
     * <p>
     * This will:
     * <ol>
     * <li>Index a single document with a field named "value" whose value is set to {@code expectedValue}.</li>
     * <li>Use the {@link #indexName} to snapshot and nothing else.</li>
     * <li>Wait until the snapshot is created before returning and it will assert that it has been created.</li>
     * </ol>
     *
     * @param repoName The name of the repository to create.
     * @param snapshotName The name of the snapshot to create.
     * @param expectedValue A value that can be tested against after restoring from the snapshot.
     * @return The randomly generated ID used to index the {@code expectedValue}. Never {@code null}.
     *
     * @see #createRepositoryAndSnapshot(String, String)
     */
    String createRepositoryAndSnapshot(String repoName, String snapshotName, String expectedValue) {
        // Create the repository for the snapshot
        createRepository(repoName)

        // Write a document
        String docId = indexDoc(indexName, typeName) { value = expectedValue }
        // flush the index to disk
        client.admin.indices.flushAsync { indices indexName }.actionGet()

        // Create the snapshot
        CreateSnapshotResponse createResponse = clusterAdminClient.createSnapshotSync {
            repository repoName
            snapshot snapshotName
            indices indexName
            waitForCompletion true
        }

        assert createResponse.snapshotInfo.state() == SnapshotState.SUCCESS

        docId
    }

    /**
     * Create a repository with the {@code repoName}
     * <p>
     * This will wait until the repository is created before returning and it will assert that it has been created.
     *
     * @param repoName The name of the repository to create.
     * @return Never {@code null}. The absolute path used by the file system-based repository.
     */
    String createRepository(String repoName) {
        String absolutePath = randomRepoPath().toAbsolutePath()

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

        absolutePath
    }
}
