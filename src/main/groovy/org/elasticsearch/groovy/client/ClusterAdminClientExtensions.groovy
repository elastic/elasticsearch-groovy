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

import org.elasticsearch.action.ListenableActionFuture
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse
import org.elasticsearch.action.admin.cluster.node.hotthreads.NodesHotThreadsRequest
import org.elasticsearch.action.admin.cluster.node.hotthreads.NodesHotThreadsResponse
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequest
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse
import org.elasticsearch.action.admin.cluster.repositories.delete.DeleteRepositoryRequest
import org.elasticsearch.action.admin.cluster.repositories.delete.DeleteRepositoryResponse
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequest
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequest
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryResponse
import org.elasticsearch.action.admin.cluster.reroute.ClusterRerouteRequest
import org.elasticsearch.action.admin.cluster.reroute.ClusterRerouteResponse
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequest
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsResponse
import org.elasticsearch.action.admin.cluster.shards.ClusterSearchShardsRequest
import org.elasticsearch.action.admin.cluster.shards.ClusterSearchShardsResponse
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequest
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequest
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequest
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequest
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.status.SnapshotsStatusRequest
import org.elasticsearch.action.admin.cluster.snapshots.status.SnapshotsStatusResponse
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse
import org.elasticsearch.action.admin.cluster.tasks.PendingClusterTasksRequest
import org.elasticsearch.action.admin.cluster.tasks.PendingClusterTasksResponse
import org.elasticsearch.client.AdminClient
import org.elasticsearch.client.ClusterAdminClient
import org.elasticsearch.client.Requests

/**
 * {@code ClusterAdminClientExtensions} provides Groovy-friendly access to {@link ClusterAdminClient} features.
 * <p>
 * The future of these {@code Client} extensions is to offer synchronous-by-default methods that do not require calling
 * {@code actionGet} for your preferred method. Because the Groovy client exists as an extension of the Java code base,
 * we decided that we would <em>not</em> dramatically change any API calls without an extremely good reason (e.g.,
 * something simply does not work). To that effect, we have added a transition step to the Groovy client that does
 * not exist in the Java client so that you can prepare for the synchronous methods by adding a companion method to
 * every single {@code Client} <em>request</em> that ends in {@code Sync}, such as
 * {@link ClusterAdminClientExtensions#healthSync}, which is the synchronous alternative to
 * {@link ClusterAdminClient#health}.
 * <p>
 * The long term goal is to remove the {@code *Sync} options and to make the normal requests (those lacking any
 * {@code *Sync} or {@code *Async} suffix) synchronous.
 * <p>
 * If you choose to continue using asynchronous methods, then you are strongly encouraged to adopt the new {@code Async}
 * companion methods to avoid all possible confusion, as well as to aid with any eventual transition.
 * @see AdminClient#cluster()
 */
class ClusterAdminClientExtensions extends AbstractClientExtensions {

    // REQUEST/RESPONSE

    /**
     * Get the health of the cluster.
     * <p>
     * The Health API can be useful for infrastructure checkups (aka health checkups, hence the API's name). It can
     * be used to determine the cluster health right now or, often more usefully, it can block until it observes a
     * specific state of interest (e.g., a degraded state):
     * <pre>
     * ClusterHealthResponse response = client.admin.cluster.healthSync {
     *     waitForStatus ClusterHealthStatus.YELLOW
     * }
     * </pre>
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterHealthRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ClusterHealthResponse healthSync(ClusterAdminClient self, Closure requestClosure) {
        doRequest(self, Requests.clusterHealthRequest(), requestClosure, self.&health)
    }

    /**
     * Get the health of the cluster.
     * <p>
     * The Health API can be useful for infrastructure checkups (aka health checkups, hence the API's name). It can
     * be used to determine the cluster health right now or, often more usefully, it can block until it observes a
     * specific state of interest (e.g., a degraded state):
     * <pre>
     * ClusterHealthResponse response = client.admin.cluster.health {
     *     waitForStatus ClusterHealthStatus.YELLOW
     * }.actionGet()
     * </pre>
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterHealthRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ClusterHealthResponse> health(ClusterAdminClient self, Closure requestClosure) {
        doRequestAsync(self, Requests.clusterHealthRequest(), requestClosure, self.&health)
    }

    /**
     * Get the health of the cluster.
     * <p>
     * The Health API can be useful for infrastructure checkups (aka health checkups, hence the API's name). It can
     * be used to determine the cluster health right now or, often more usefully, it can block until it observes a
     * specific state of interest (e.g., a degraded state):
     * <pre>
     * ClusterHealthResponse response = client.admin.cluster.healthAsync {
     *     waitForStatus ClusterHealthStatus.YELLOW
     * }.actionGet()
     * </pre>
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterHealthRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ClusterHealthResponse> healthAsync(ClusterAdminClient self, Closure requestClosure) {
        doRequestAsync(self, Requests.clusterHealthRequest(), requestClosure, self.&health)
    }

    /**
     * Get the state of the cluster.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterStateRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ClusterStateResponse stateSync(ClusterAdminClient self, Closure requestClosure) {
        doRequest(self, Requests.clusterStateRequest(), requestClosure, self.&state)
    }

    /**
     * Get the state of the cluster.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterStateRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ClusterStateResponse> state(ClusterAdminClient self, Closure requestClosure) {
        doRequestAsync(self, Requests.clusterStateRequest(), requestClosure, self.&state)
    }

    /**
     * Get the state of the cluster.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterStateRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ClusterStateResponse> stateAsync(ClusterAdminClient self, Closure requestClosure) {
        doRequestAsync(self, Requests.clusterStateRequest(), requestClosure, self.&state)
    }

    /**
     * Update settings in the cluster.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterUpdateSettingsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ClusterUpdateSettingsResponse> updateSettingsSync(ClusterAdminClient self,
                                                                                    Closure requestClosure) {
        doRequest(self, Requests.clusterUpdateSettingsRequest(), requestClosure, self.&updateSettings)
    }

    /**
     * Update settings in the cluster.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterUpdateSettingsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ClusterUpdateSettingsResponse> updateSettings(ClusterAdminClient self,
                                                                                Closure requestClosure) {
        doRequestAsync(self, Requests.clusterUpdateSettingsRequest(), requestClosure, self.&updateSettings)
    }

    /**
     * Update settings in the cluster.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterUpdateSettingsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ClusterUpdateSettingsResponse> updateSettingsAsync(ClusterAdminClient self,
                                                                                     Closure requestClosure) {
        doRequestAsync(self, Requests.clusterUpdateSettingsRequest(), requestClosure, self.&updateSettings)
    }

    /**
     * Reroute the allocation of shards in the cluster.
     * <p>
     * Note: This is an Advanced API and care should be taken before performing related operations.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterRerouteRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ClusterRerouteResponse rerouteSync(ClusterAdminClient self, Closure requestClosure) {
        doRequest(self, Requests.clusterRerouteRequest(), requestClosure, self.&reroute)
    }

    /**
     * Reroute the allocation of shards in the cluster.
     * <p>
     * Note: This is an Advanced API and care should be taken before performing related operations.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterRerouteRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ClusterRerouteResponse> reroute(ClusterAdminClient self, Closure requestClosure) {
        doRequestAsync(self, Requests.clusterRerouteRequest(), requestClosure, self.&reroute)
    }

    /**
     * Reroute the allocation of shards in the cluster.
     * <p>
     * Note: This is an Advanced API and care should be taken before performing related operations.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterRerouteRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ClusterRerouteResponse> rerouteAsync(ClusterAdminClient self,
                                                                       Closure requestClosure) {
        doRequestAsync(self, Requests.clusterRerouteRequest(), requestClosure, self.&reroute)
    }

    /**
     * Get the cluster-wide aggregated stats.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterStatsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ClusterStatsResponse clusterStatsSync(ClusterAdminClient self, Closure requestClosure) {
        doRequest(self, Requests.clusterStatsRequest(), requestClosure, self.&clusterStats)
    }

    /**
     * Get the cluster-wide aggregated stats.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterStatsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ClusterStatsResponse> clusterStats(ClusterAdminClient self, Closure requestClosure) {
        doRequestAsync(self, Requests.clusterStatsRequest(), requestClosure, self.&clusterStats)
    }


    /**
     * Get the cluster-wide aggregated stats.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterStatsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ClusterStatsResponse> clusterStatsAsync(ClusterAdminClient self,
                                                                          Closure requestClosure) {
        doRequestAsync(self, Requests.clusterStatsRequest(), requestClosure, self.&clusterStats)
    }

    /**
     * Get the nodes info of the cluster.
     * <p>
     * NodesInfoResponse response = clusterAdminClient.nodesInfoSync {
     *     all() // collect all info possible
     * }
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link NodesInfoRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static NodesInfoResponse nodesInfoSync(ClusterAdminClient self, Closure requestClosure) {
        doRequest(self, Requests.nodesInfoRequest(), requestClosure, self.&nodesInfo)
    }

    /**
     * Get the nodes info of the cluster.
     * <p>
     * NodesInfoResponse response = clusterAdminClient.nodesInfo {
     *     all() // collect all info possible
     * }.actionGet()
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link NodesInfoRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<NodesInfoResponse> nodesInfo(ClusterAdminClient self, Closure requestClosure) {
        doRequestAsync(self, Requests.nodesInfoRequest(), requestClosure, self.&nodesInfo)
    }

    /**
     * Get the nodes info of the cluster.
     * <p>
     * NodesInfoResponse response = clusterAdminClient.nodesInfoAsync {
     *     all() // collect all info possible
     * }.actionGet()
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link NodesInfoRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<NodesInfoResponse> nodesInfoAsync(ClusterAdminClient self, Closure requestClosure) {
        doRequestAsync(self, Requests.nodesInfoRequest(), requestClosure, self.&nodesInfo)
    }

    /**
     * Get the nodes stats of the cluster.
     * <pre>
     * NodesStatsResponse response = clusterAdminClient.nodesStatsSync {
     *     all() // collect all stats possible
     * }
     * </pre>
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link NodesStatsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static NodesStatsResponse nodesStatsSync(ClusterAdminClient self, Closure requestClosure) {
        doRequest(self, Requests.nodesStatsRequest(), requestClosure, self.&nodesStats)
    }

    /**
     * Get the nodes stats of the cluster.
     * <pre>
     * NodesStatsResponse response = clusterAdminClient.nodesStats {
     *     all() // collect all stats possible
     * }.actionGet()
     * </pre>
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link NodesStatsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<NodesStatsResponse> nodesStats(ClusterAdminClient self, Closure requestClosure) {
        doRequestAsync(self, Requests.nodesStatsRequest(), requestClosure, self.&nodesStats)
    }

    /**
     * Get the nodes stats of the cluster.
     * <pre>
     * NodesStatsResponse response = clusterAdminClient.nodesStatsAsync {
     *     all() // collect all stats possible
     * }.actionGet()
     * </pre>
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link NodesStatsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<NodesStatsResponse> nodesStatsAsync(ClusterAdminClient self, Closure requestClosure) {
        doRequestAsync(self, Requests.nodesStatsRequest(), requestClosure, self.&nodesStats)
    }

    /**
     * Get the hot threads details from nodes in the cluster.
     * <p>
     * This is particularly useful when you feel the need to debug activity on the cluster, so this can be requested
     * and logged. To get a reasonable number of threads (above the conservative default), you might do something like:
     * <pre>
     * NodesHotThreadsResponse response = client.admin.cluster.nodesHotThreads {
     *     threads 500
     * }.actionGet()
     * </pre>
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link NodesHotThreadsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static NodesHotThreadsResponse nodesHotThreadsSync(ClusterAdminClient self, Closure requestClosure) {
        doRequest(self, new NodesHotThreadsRequest(), requestClosure, self.&nodesHotThreads)
    }

    /**
     * Get the hot threads details from nodes in the cluster.
     * <p>
     * This is particularly useful when you feel the need to debug activity on the cluster, so this can be requested
     * and logged. To get a reasonable number of threads (above the conservative default), you might do something like:
     * <pre>
     * NodesHotThreadsResponse response = client.admin.cluster.nodesHotThreads {
     *     threads 500
     * }.actionGet()
     * </pre>
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link NodesHotThreadsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<NodesHotThreadsResponse> nodesHotThreads(ClusterAdminClient self,
                                                                                Closure requestClosure) {
        doRequestAsync(self, new NodesHotThreadsRequest(), requestClosure, self.&nodesHotThreads)
    }

    /**
     * Get the hot threads details from nodes in the cluster.
     * <p>
     * This is particularly useful when you feel the need to debug activity on the cluster, so this can be requested
     * and logged. To get a reasonable number of threads (above the conservative default), you might do something like:
     * <pre>
     * NodesHotThreadsResponse response = client.admin.cluster.nodesHotThreads {
     *     threads 500
     * }.actionGet()
     * </pre>
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link NodesHotThreadsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<NodesHotThreadsResponse> nodesHotThreadsAsync(ClusterAdminClient self,
                                                                                Closure requestClosure) {
        doRequestAsync(self, new NodesHotThreadsRequest(), requestClosure, self.&nodesHotThreads)
    }

    /**
     * Determine the shards that the given search would be executed on within the cluster.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterSearchShardsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ClusterSearchShardsResponse searchShardsSync(ClusterAdminClient self, Closure requestClosure) {
        doRequest(self, Requests.clusterSearchShardsRequest(), requestClosure, self.&searchShards)
    }

    /**
     * Determine the shards that the given search would be executed on within the cluster.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterSearchShardsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ClusterSearchShardsResponse> searchShards(ClusterAdminClient self,
                                                                            Closure requestClosure) {
        doRequestAsync(self, Requests.clusterSearchShardsRequest(), requestClosure, self.&searchShards)
    }

    /**
     * Determine the shards that the given search would be executed on within the cluster.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClusterSearchShardsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ClusterSearchShardsResponse> searchShardsAsync(ClusterAdminClient self,
                                                                                 Closure requestClosure) {
        doRequestAsync(self, Requests.clusterSearchShardsRequest(), requestClosure, self.&searchShards)
    }

    /**
     * Register a snapshot repository.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link PutRepositoryRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static PutRepositoryResponse putRepositorySync(ClusterAdminClient self, Closure requestClosure) {
        // closure is expected to set the repo name
        doRequest(self, Requests.putRepositoryRequest(null), requestClosure, self.&putRepository)
    }

    /**
     * Register a snapshot repository.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link PutRepositoryRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<PutRepositoryResponse> putRepository(ClusterAdminClient self,
                                                                       Closure requestClosure) {
        // closure is expected to set the repo name
        doRequestAsync(self, Requests.putRepositoryRequest(null), requestClosure, self.&putRepository)
    }

    /**
     * Register a snapshot repository.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link PutRepositoryRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<PutRepositoryResponse> putRepositoryAsync(ClusterAdminClient self,
                                                                            Closure requestClosure) {
        // closure is expected to set the repo name
        doRequestAsync(self, Requests.putRepositoryRequest(null), requestClosure, self.&putRepository)
    }

    /**
     * Unregister a snapshot repository.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link DeleteRepositoryRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static DeleteRepositoryResponse deleteRepositorySync(ClusterAdminClient self, Closure requestClosure) {
        // closure is expected to set the repo name
        doRequest(self, Requests.deleteRepositoryRequest(null), requestClosure, self.&deleteRepository)
    }

    /**
     * Unregister a snapshot repository.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link DeleteRepositoryRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<DeleteRepositoryResponse> deleteRepository(ClusterAdminClient self,
                                                                             Closure requestClosure) {
        // closure is expected to set the repo name
        doRequestAsync(self, Requests.deleteRepositoryRequest(null), requestClosure, self.&deleteRepository)
    }

    /**
     * Unregister a snapshot repository.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link DeleteRepositoryRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<DeleteRepositoryResponse> deleteRepositoryAsync(ClusterAdminClient self,
                                                                                  Closure requestClosure) {
        // closure is expected to set the repo name
        doRequestAsync(self, Requests.deleteRepositoryRequest(null), requestClosure, self.&deleteRepository)
    }

    /**
     * Get snapshot repositories.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetRepositoriesRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static GetRepositoriesResponse getRepositoriesSync(ClusterAdminClient self, Closure requestClosure) {
        doRequest(self, Requests.getRepositoryRequest(), requestClosure, self.&getRepositories)
    }

    /**
     * Get snapshot repositories.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetRepositoriesRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetRepositoriesResponse> getRepositories(ClusterAdminClient self,
                                                                           Closure requestClosure) {
        doRequestAsync(self, Requests.getRepositoryRequest(), requestClosure, self.&getRepositories)
    }

    /**
     * Get snapshot repositories.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetRepositoriesRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetRepositoriesResponse> getRepositoriesAsync(ClusterAdminClient self,
                                                                                Closure requestClosure) {
        doRequestAsync(self, Requests.getRepositoryRequest(), requestClosure, self.&getRepositories)
    }

    /**
     * Create a new snapshot in a repository.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link CreateSnapshotRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static CreateSnapshotResponse createSnapshotSync(ClusterAdminClient self, Closure requestClosure) {
        // closure is expected to set the repo and snapshot names
        doRequest(self, Requests.createSnapshotRequest(null, null), requestClosure, self.&createSnapshot)
    }

    /**
     * Create a new snapshot in a repository.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link CreateSnapshotRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<CreateSnapshotResponse> createSnapshot(ClusterAdminClient self,
                                                                         Closure requestClosure) {
        // closure is expected to set the repo and snapshot names
        doRequestAsync(self, Requests.createSnapshotRequest(null, null), requestClosure, self.&createSnapshot)
    }

    /**
     * Create a new snapshot in a repository.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link CreateSnapshotRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<CreateSnapshotResponse> createSnapshotAsync(ClusterAdminClient self,
                                                                              Closure requestClosure) {
        // closure is expected to set the repo and snapshot names
        doRequestAsync(self, Requests.createSnapshotRequest(null, null), requestClosure, self.&createSnapshot)
    }

    /**
     * Get the snapshots status from a repository.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link SnapshotsStatusRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static SnapshotsStatusResponse snapshotsStatusSync(ClusterAdminClient self, Closure requestClosure) {
        // closure is expected to set the repo name
        doRequest(self, Requests.snapshotsStatusRequest(null), requestClosure, self.&snapshotsStatus)
    }

    /**
     * Get the snapshots status from a repository.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link SnapshotsStatusRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<SnapshotsStatusResponse> snapshotsStatus(ClusterAdminClient self,
                                                                           Closure requestClosure) {
        // closure is expected to set the repo name
        doRequestAsync(self, Requests.snapshotsStatusRequest(null), requestClosure, self.&snapshotsStatus)
    }

    /**
     * Get the snapshots status from a repository.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link SnapshotsStatusRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<SnapshotsStatusResponse> snapshotsStatusAsync(ClusterAdminClient self,
                                                                                Closure requestClosure) {
        // closure is expected to set the repo name
        doRequestAsync(self, Requests.snapshotsStatusRequest(null), requestClosure, self.&snapshotsStatus)
    }

    /**
     * Get snapshots from a repository.
     * <pre>
     * GetSnapshotsResponse response = clusterAdminClient.getSnapshotsSync {
     *     repository "my-repo-name"
     * }
     * </pre>
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetSnapshotsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static GetSnapshotsResponse getSnapshotsSync(ClusterAdminClient self, Closure requestClosure) {
        // closure is expected to set the repo name
        doRequest(self, Requests.getSnapshotsRequest(null), requestClosure, self.&getSnapshots)
    }

    /**
     * Get snapshots from a repository.
     * <pre>
     * GetSnapshotsResponse response = clusterAdminClient.getSnapshots {
     *     repository "my-repo-name"
     * }.actionGet()
     * </pre>
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetSnapshotsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetSnapshotsResponse> getSnapshots(ClusterAdminClient self,
                                                                     Closure requestClosure) {
        // closure is expected to set the repo name
        doRequestAsync(self, Requests.getSnapshotsRequest(null), requestClosure, self.&getSnapshots)
    }

    /**
     * Get snapshots from a repository.
     * <pre>
     * GetSnapshotsResponse response = clusterAdminClient.getSnapshotsAsync {
     *     repository "my-repo-name"
     * }.actionGet()
     * </pre>
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetSnapshotsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetSnapshotsResponse> getSnapshotsAsync(ClusterAdminClient self,
                                                                          Closure requestClosure) {
        // closure is expected to set the repo name
        doRequestAsync(self, Requests.getSnapshotsRequest(null), requestClosure, self.&getSnapshots)
    }

    /**
     * Restore from a snapshot in a repository.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link RestoreSnapshotRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static RestoreSnapshotResponse restoreSnapshotSync(ClusterAdminClient self, Closure requestClosure) {
        // closure is expected to set the repo and snapshot names
        doRequest(self, Requests.restoreSnapshotRequest(null, null), requestClosure, self.&restoreSnapshot)
    }

    /**
     * Restore from a snapshot in a repository.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link RestoreSnapshotRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<RestoreSnapshotResponse> restoreSnapshot(ClusterAdminClient self,
                                                                           Closure requestClosure) {
        // closure is expected to set the repo and snapshot names
        doRequestAsync(self, Requests.restoreSnapshotRequest(null, null), requestClosure, self.&restoreSnapshot)
    }

    /**
     * Restore from a snapshot in a repository.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link RestoreSnapshotRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<RestoreSnapshotResponse> restoreSnapshotAsync(ClusterAdminClient self,
                                                                                Closure requestClosure) {
        // closure is expected to set the repo and snapshot names
        doRequestAsync(self, Requests.restoreSnapshotRequest(null, null), requestClosure, self.&restoreSnapshot)
    }

    /**
     * Delete snapshots from a repository.
     * <p>
     * Note: Deleting a snapshot should only be done after creating newer snapshots, which are themselves backed up, in
     * order to avoid data loss.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link DeleteSnapshotRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static DeleteSnapshotResponse deleteSnapshotSync(ClusterAdminClient self, Closure requestClosure) {
        // closure is expected to set the repo and snapshot names
        doRequest(self, Requests.deleteSnapshotRequest(null, null), requestClosure, self.&deleteSnapshot)
    }

    /**
     * Delete snapshots from a repository.
     * <p>
     * Note: Deleting a snapshot should only be done after creating newer snapshots, which are themselves backed up, in
     * order to avoid data loss.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link DeleteSnapshotRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<DeleteSnapshotResponse> deleteSnapshot(ClusterAdminClient self,
                                                                         Closure requestClosure) {
        // closure is expected to set the repo and snapshot names
        doRequestAsync(self, Requests.deleteSnapshotRequest(null, null), requestClosure, self.&deleteSnapshot)
    }

    /**
     * Delete snapshots from a repository.
     * <p>
     * Note: Deleting a snapshot should only be done after creating newer snapshots, which are themselves backed up, in
     * order to avoid data loss.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link DeleteSnapshotRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<DeleteSnapshotResponse> deleteSnapshotAsync(ClusterAdminClient self,
                                                                              Closure requestClosure) {
        // closure is expected to set the repo and snapshot names
        doRequestAsync(self, Requests.deleteSnapshotRequest(null, null), requestClosure, self.&deleteSnapshot)
    }

    /**
     * Get a list of pending cluster tasks that are scheduled to be executed.
     * <p>
     * This includes operations that update the cluster state (e.g., a create index operation).
     * <pre>
     * PendingClusterTasksResponse response = client.admin.cluster.pendingClusterTasksSync {
     *     // There are currently no parameters for the request
     * }
     * </pre>
     * This is particularly useful when attempting to understand why a cluster's master is showing slow responses.
     * Specifically, it's useful to check this if the cluster's {@link #health} is yellow or red.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link PendingClusterTasksRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static PendingClusterTasksResponse pendingClusterTasksSync(ClusterAdminClient self, Closure requestClosure) {
        doRequest(self, new PendingClusterTasksRequest(), requestClosure, self.&pendingClusterTasks)
    }

    /**
     * Get a list of pending cluster tasks that are scheduled to be executed.
     * <p>
     * This includes operations that update the cluster state (e.g., a create index operation).
     * <pre>
     * PendingClusterTasksResponse response = client.admin.cluster.pendingClusterTasks {
     *     // There are currently no parameters for the request
     * }.actionGet()
     * </pre>
     * This is particularly useful when attempting to understand why a cluster's master is showing slow responses.
     * Specifically, it's useful to check this if the cluster's {@link #health} is yellow or red.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link PendingClusterTasksRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<PendingClusterTasksResponse> pendingClusterTasks(ClusterAdminClient self,
                                                                                   Closure requestClosure) {
        doRequestAsync(self, new PendingClusterTasksRequest(), requestClosure, self.&pendingClusterTasks)
    }

    /**
     * Get a list of pending cluster tasks that are scheduled to be executed.
     * <p>
     * This includes operations that update the cluster state (e.g., a create index operation).
     * <pre>
     * PendingClusterTasksResponse response = client.admin.cluster.pendingClusterTasksAsync {
     *     // There are currently no parameters for the request
     * }.actionGet()
     * </pre>
     * This is particularly useful when attempting to understand why a cluster's master is showing slow responses.
     * Specifically, it's useful to check this if the cluster's {@link #health} is yellow or red.
     *
     * @param self The {@code this} reference for the {@link ClusterAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link PendingClusterTasksRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<PendingClusterTasksResponse> pendingClusterTasksAsync(ClusterAdminClient self,
                                                                                        Closure requestClosure) {
        doRequestAsync(self, new PendingClusterTasksRequest(), requestClosure, self.&pendingClusterTasks)
    }
}
