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
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse
import org.elasticsearch.action.admin.indices.alias.exists.AliasesExistResponse
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse
import org.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheRequest
import org.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheResponse
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse
import org.elasticsearch.action.admin.indices.flush.FlushRequest
import org.elasticsearch.action.admin.indices.flush.FlushResponse
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingRequest
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsRequest
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse
import org.elasticsearch.action.admin.indices.optimize.OptimizeRequest
import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse
import org.elasticsearch.action.admin.indices.recovery.RecoveryRequest
import org.elasticsearch.action.admin.indices.recovery.RecoveryResponse
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentResponse
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentsRequest
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateRequest
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateResponse
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesRequest
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesResponse
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryRequest
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryResponse
import org.elasticsearch.action.admin.indices.warmer.delete.DeleteWarmerRequest
import org.elasticsearch.action.admin.indices.warmer.delete.DeleteWarmerResponse
import org.elasticsearch.action.admin.indices.warmer.get.GetWarmersRequest
import org.elasticsearch.action.admin.indices.warmer.get.GetWarmersResponse
import org.elasticsearch.action.admin.indices.warmer.put.PutWarmerRequest
import org.elasticsearch.action.admin.indices.warmer.put.PutWarmerResponse
import org.elasticsearch.client.AdminClient
import org.elasticsearch.client.IndicesAdminClient
import org.elasticsearch.client.Requests

/**
 * {@code IndicesAdminClientExtensions} provides Groovy-friendly access to {@link IndicesAdminClient} features.
 * @see AdminClient#indices()
 */
class IndicesAdminClientExtensions extends AbstractClientExtensions {

    // REQUEST/RESPONSE

    /**
     * Explicitly refresh one or more indices, which makes all content indexed since the last refresh searchable.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link RefreshRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#refreshAsync}.
     */
    @Deprecated
    static ListenableActionFuture<RefreshResponse> refresh(IndicesAdminClient self, Closure requestClosure) {
        refreshAsync(self, requestClosure)
    }

    /**
     * Explicitly refresh one or more indices, which makes all content indexed since the last refresh searchable.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link RefreshRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<RefreshResponse> refreshAsync(IndicesAdminClient self, Closure requestClosure) {
        doRequestAsync(self, Requests.refreshRequest(), requestClosure, self.&refresh)
    }

    /**
     * Determine if the specified indices exist.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link IndicesExistsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#existsAsync}.
     */
    @Deprecated
    static ListenableActionFuture<IndicesExistsResponse> exists(IndicesAdminClient self, Closure requestClosure) {
        existsAsync(self, requestClosure)
    }

    /**
     * Determine if the specified indices exist.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link IndicesExistsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<IndicesExistsResponse> existsAsync(IndicesAdminClient self, Closure requestClosure) {
        doRequestAsync(self, Requests.indicesExistsRequest(), requestClosure, self.&exists)
    }

    /**
     * Determine if the specified types exist within the specified indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link TypesExistsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#typesExistsAsync}.
     */
    @Deprecated
    static ListenableActionFuture<TypesExistsResponse> typesExists(IndicesAdminClient self, Closure requestClosure) {
        typesExistsAsync(self, requestClosure)
    }

    /**
     * Determine if the specified types exist within the specified indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link TypesExistsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<TypesExistsResponse> typesExistsAsync(IndicesAdminClient self,
                                                                        Closure requestClosure) {
        // indices must be supplied by the closure
        doRequestAsync(self, new TypesExistsRequest(null), requestClosure, self.&typesExists)
    }

    /**
     * Get stats for indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link IndicesStatsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#statsAsync}.
     */
    @Deprecated
    static ListenableActionFuture<IndicesStatsResponse> stats(IndicesAdminClient self, Closure requestClosure) {
        statsAsync(self, requestClosure)
    }

    /**
     * Get stats for indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link IndicesStatsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<IndicesStatsResponse> statsAsync(IndicesAdminClient self, Closure requestClosure) {
        doRequestAsync(self, new IndicesStatsRequest(), requestClosure, self.&stats)
    }

    /**
     * Get details pertaining to the recovery state of indices and their associated shards.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link RecoveryRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#recoveriesAsync}.
     */
    @Deprecated
    static ListenableActionFuture<RecoveryResponse> recoveries(IndicesAdminClient self, Closure requestClosure) {
        recoveriesAsync(self, requestClosure)
    }

    /**
     * Get details pertaining to the recovery state of indices and their associated shards.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link RecoveryRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<RecoveryResponse> recoveriesAsync(IndicesAdminClient self, Closure requestClosure) {
        doRequestAsync(self, new RecoveryRequest(), requestClosure, self.&recoveries)
    }

    /**
     * Get details pertaining to the segments of indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link IndicesSegmentsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#segmentsAsync}.
     */
    @Deprecated
    static ListenableActionFuture<IndicesSegmentResponse> segments(IndicesAdminClient self, Closure requestClosure) {
        segmentsAsync(self, requestClosure)
    }

    /**
     * Get details pertaining to the segments of indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link IndicesSegmentsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<IndicesSegmentResponse> segmentsAsync(IndicesAdminClient self,
                                                                        Closure requestClosure) {
        doRequestAsync(self, Requests.indicesExistsRequest(), requestClosure, self.&segments)
    }

    /**
     * Create an index explicitly, which allows the index configuration to be specified.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link CreateIndexRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#createAsync}.
     */
    @Deprecated
    static ListenableActionFuture<CreateIndexResponse> create(IndicesAdminClient self, Closure requestClosure) {
        createAsync(self, requestClosure)
    }

    /**
     * Create an index explicitly, which allows the index configuration to be specified.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link CreateIndexRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<CreateIndexResponse> createAsync(IndicesAdminClient self, Closure requestClosure) {
        // index must be set by the closure
        doRequestAsync(self, Requests.createIndexRequest(null), requestClosure, self.&create)
    }

    /**
     * Delete the specified indices.
     * <p />
     * Note: Manually supply the reserved index name of "_all" to delete all indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link DeleteIndexRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#deleteAsync}.
     */
    @Deprecated
    static ListenableActionFuture<DeleteIndexResponse> delete(IndicesAdminClient self, Closure requestClosure) {
        deleteAsync(self, requestClosure)
    }

    /**
     * Delete the specified indices.
     * <p />
     * Note: Manually supply the reserved index name of "_all" to delete all indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link DeleteIndexRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<DeleteIndexResponse> deleteAsync(IndicesAdminClient self, Closure requestClosure) {
        // index must be set by the closure
        doRequestAsync(self, Requests.deleteIndexRequest(null), requestClosure, self.&delete)
    }

    /**
     * Close the specified indices. Closing an index prevents documents from being added, updated, or removed. This is
     * a good way to make an index read-only.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link CloseIndexRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#closeAsync}.
     */
    @Deprecated
    static ListenableActionFuture<CloseIndexResponse> close(IndicesAdminClient self, Closure requestClosure) {
        closeAsync(self, requestClosure)
    }

    /**
     * Close the specified indices. Closing an index prevents documents from being added, updated, or removed. This is
     * a good way to make an index read-only.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link CloseIndexRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<CloseIndexResponse> closeAsync(IndicesAdminClient self, Closure requestClosure) {
        // index must be set by the closure
        doRequestAsync(self, Requests.closeIndexRequest(null), requestClosure, self.&close)
    }

    /**
     * Open the specified indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link OpenIndexRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#openAsync}.
     */
    @Deprecated
    static ListenableActionFuture<OpenIndexResponse> open(IndicesAdminClient self, Closure requestClosure) {
        openAsync(self, requestClosure)
    }

    /**
     * Open the specified indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link OpenIndexRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<OpenIndexResponse> openAsync(IndicesAdminClient self, Closure requestClosure) {
        // index must be set by the closure
        doRequestAsync(self, Requests.openIndexRequest(null), requestClosure, self.&open)
    }

    /**
     * Explicitly flush the specified indices. A successful flush of an index guarantees that items in its transaction
     * log have been written to disk and starts a new transaction log.
     * <p />
     * Note: By default, Elasticsearch will perform flush operations automatically.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link FlushRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#flushAsync}.
     */
    @Deprecated
    static ListenableActionFuture<FlushResponse> flush(IndicesAdminClient self, Closure requestClosure) {
        flushAsync(self, requestClosure)
    }

    /**
     * Explicitly flush the specified indices. A successful flush of an index guarantees that items in its transaction
     * log have been written to disk and starts a new transaction log.
     * <p />
     * Note: By default, Elasticsearch will perform flush operations automatically.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link FlushRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<FlushResponse> flushAsync(IndicesAdminClient self, Closure requestClosure) {
        doRequestAsync(self, Requests.flushRequest(), requestClosure, self.&flush)
    }

    /**
     * Explicitly optimize the specified indices.
     * <p />
     * Optimizing an index will reduce the number of segments that the index contains, which will speed up future search
     * operations. Like other operations, Elasticsearch will automatically optimize indices in the background.
     * <p />
     * The optimal number of segments is <tt>1</tt>, but an active index will regularly have more than <tt>1</tt>. A
     * {@link IndicesAdminClient#close(CloseIndexRequest) closed} index can be safely optimized to <tt>1</tt> segment to
     * speed up future search operations.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link OptimizeRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#optimizeAsync}.
     */
    @Deprecated
    static ListenableActionFuture<OptimizeResponse> optimize(IndicesAdminClient self, Closure requestClosure) {
        optimizeAsync(self, requestClosure)
    }

    /**
     * Explicitly optimize the specified indices.
     * <p />
     * Optimizing an index will reduce the number of segments that the index contains, which will speed up future search
     * operations. Like other operations, Elasticsearch will automatically optimize indices in the background.
     * <p />
     * The optimal number of segments is <tt>1</tt>, but an active index will regularly have more than <tt>1</tt>. A
     * {@link IndicesAdminClient#close(CloseIndexRequest) closed} index can be safely optimized to <tt>1</tt> segment to
     * speed up future search operations.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link OptimizeRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<OptimizeResponse> optimizeAsync(IndicesAdminClient self, Closure requestClosure) {
        doRequestAsync(self, Requests.optimizeRequest(), requestClosure, self.&optimize)
    }

    /**
     * Get the mappings of one or more types.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetMappingsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#getMappingsAsync}.
     */
    @Deprecated
    static ListenableActionFuture<GetMappingsResponse> getMappings(IndicesAdminClient self, Closure requestClosure) {
        getMappingsAsync(self, requestClosure)
    }

    /**
     * Get the mappings of one or more types.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetMappingsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetMappingsResponse> getMappingsAsync(IndicesAdminClient self, Closure requestClosure) {
        doRequestAsync(self, new GetMappingsRequest(), requestClosure, self.&getMappings)
    }

    /**
     * Get the mappings of one or more fields.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetFieldMappingsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#getFieldMappingsAsync}.
     */
    @Deprecated
    static ListenableActionFuture<GetFieldMappingsResponse> getFieldMappings(IndicesAdminClient self,
                                                                             Closure requestClosure) {
        getFieldMappingsAsync(self, requestClosure)
    }

    /**
     * Get the mappings of one or more fields.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetFieldMappingsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetFieldMappingsResponse> getFieldMappingsAsync(IndicesAdminClient self,
                                                                                  Closure requestClosure) {
        doRequestAsync(self, new GetFieldMappingsRequest(), requestClosure, self.&getFieldMappings)
    }

    /**
     * Add the mapping definition for a type into one or more indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link PutMappingRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#putMappingAsync}.
     */
    @Deprecated
    static ListenableActionFuture<PutMappingResponse> putMapping(IndicesAdminClient self, Closure requestClosure) {
        putMappingAsync(self, requestClosure)
    }

    /**
     * Add the mapping definition for a type into one or more indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link PutMappingRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<PutMappingResponse> putMappingAsync(IndicesAdminClient self, Closure requestClosure) {
        doRequestAsync(self, Requests.putMappingRequest(), requestClosure, self.&putMapping)
    }

    /**
     * Delete the mapping definition for a type in one or more indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link DeleteMappingRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#deleteMappingAsync}.
     */
    @Deprecated
    static ListenableActionFuture<DeleteMappingResponse> deleteMapping(IndicesAdminClient self,
                                                                       Closure requestClosure) {
        deleteMappingAsync(self, requestClosure)
    }

    /**
     * Delete the mapping definition for a type in one or more indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link DeleteMappingRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<DeleteMappingResponse> deleteMappingAsync(IndicesAdminClient self,
                                                                            Closure requestClosure) {
        doRequestAsync(self, Requests.deleteMappingRequest(), requestClosure, self.&deleteMapping)
    }

    /**
     * Atomically add or remove index aliases.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link IndicesAliasesRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#aliasesAsync}.
     */
    @Deprecated
    static ListenableActionFuture<IndicesAliasesResponse> aliases(IndicesAdminClient self, Closure requestClosure) {
        aliasesAsync(self, requestClosure)
    }

    /**
     * Atomically add or remove index aliases.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link IndicesAliasesRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<IndicesAliasesResponse> aliasesAsync(IndicesAdminClient self,
                                                                       Closure requestClosure) {
        doRequestAsync(self, Requests.indexAliasesRequest(), requestClosure, self.&aliases)
    }

    /**
     * Get index aliases.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetAliasesRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#getAliasesAsync}.
     */
    @Deprecated
    static ListenableActionFuture<GetAliasesResponse> getAliases(IndicesAdminClient self, Closure requestClosure) {
        getAliasesAsync(self, requestClosure)
    }

    /**
     * Get index aliases.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetAliasesRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetAliasesResponse> getAliasesAsync(IndicesAdminClient self, Closure requestClosure) {
        doRequestAsync(self, new GetAliasesRequest(), requestClosure, self.&getAliases)
    }

    /**
     * Determine if index aliases exist.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetAliasesRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#aliasesExistAsync}.
     */
    @Deprecated
    static ListenableActionFuture<AliasesExistResponse> aliasesExist(IndicesAdminClient self, Closure requestClosure) {
        aliasesExistAsync(self, requestClosure)
    }

    /**
     * Determine if index aliases exist.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetAliasesRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<AliasesExistResponse> aliasesExistAsync(IndicesAdminClient self,
                                                                          Closure requestClosure) {
        doRequestAsync(self, new GetAliasesRequest(), requestClosure, self.&aliasesExist)
    }

    /**
     * Clear the indices specified caches.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClearIndicesCacheRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#clearCacheAsync}.
     */
    @Deprecated
    static ListenableActionFuture<ClearIndicesCacheResponse> clearCache(IndicesAdminClient self,
                                                                        Closure requestClosure) {
        clearCacheAsync(self, requestClosure)
    }

    /**
     * Clear the indices specified caches.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClearIndicesCacheRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ClearIndicesCacheResponse> clearCacheAsync(IndicesAdminClient self,
                                                                             Closure requestClosure) {
        doRequestAsync(self, Requests.clearIndicesCacheRequest(), requestClosure, self.&clearCache)
    }

    /**
     * Update the settings of one or more indices.
     * <p />
     * Note: Some settings can only be set at the creation of an index, such as the number of shards.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link UpdateSettingsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#updateSettingsAsync}.
     */
    @Deprecated
    static ListenableActionFuture<UpdateSettingsResponse> updateSettings(IndicesAdminClient self,
                                                                         Closure requestClosure) {
        updateSettingsAsync(self, requestClosure)
    }

    /**
     * Update the settings of one or more indices.
     * <p />
     * Note: Some settings can only be set at the creation of an index, such as the number of shards.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link UpdateSettingsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<UpdateSettingsResponse> updateSettingsAsync(IndicesAdminClient self,
                                                                              Closure requestClosure) {
        doRequestAsync(self, new UpdateSettingsRequest(), requestClosure, self.&updateSettings)
    }

    /**
     * Add an index template to enable automatic type mappings.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link PutIndexTemplateRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#putTemplateAsync}.
     */
    @Deprecated
    static ListenableActionFuture<PutIndexTemplateResponse> putTemplate(IndicesAdminClient self,
                                                                        Closure requestClosure) {
        putTemplateAsync(self, requestClosure)
    }

    /**
     * Add an index template to enable automatic type mappings.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link PutIndexTemplateRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<PutIndexTemplateResponse> putTemplateAsync(IndicesAdminClient self,
                                                                             Closure requestClosure) {
        // template name expected be supplied by the closure
        doRequestAsync(self, new PutIndexTemplateRequest(null), requestClosure, self.&putTemplate)
    }

    /**
     * Delete an index template.
     * <p />
     * Note: This will <em>not</em> unmap indices that have made use of this template.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param name The name of the index template to delete.
     * @return Never {@code null}.
     * @throws NullPointerException if {@code self} is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#deleteTemplateAsync}.
     */
    @Deprecated
    static ListenableActionFuture<DeleteIndexTemplateResponse> deleteTemplate(IndicesAdminClient self, String name) {
        deleteTemplateAsync(self, name)
    }

    /**
     * Delete an index template.
     * <p />
     * Note: This will <em>not</em> unmap indices that have made use of this template.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param name The name of the index template to delete.
     * @return Never {@code null}.
     * @throws NullPointerException if {@code self} is {@code null}
     */
    static ListenableActionFuture<DeleteIndexTemplateResponse> deleteTemplateAsync(IndicesAdminClient self,
                                                                                   String name) {
        doRequestAsync(self, new DeleteIndexTemplateRequest(name), self.&deleteTemplate)
    }

    /**
     * Get index templates.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetIndexTemplatesRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#getTemplatesAsync}.
     */
    @Deprecated
    static ListenableActionFuture<GetIndexTemplatesResponse> getTemplates(IndicesAdminClient self,
                                                                          Closure requestClosure) {
        getTemplatesAsync(self, requestClosure)
    }

    /**
     * Get index templates.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetIndexTemplatesRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetIndexTemplatesResponse> getTemplatesAsync(IndicesAdminClient self,
                                                                               Closure requestClosure) {
        doRequestAsync(self, new GetIndexTemplatesRequest(), requestClosure, self.&getTemplates)
    }

    /**
     * Validate a query for correctness.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ValidateQueryRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#validateQueryAsync}.
     */
    @Deprecated
    static ListenableActionFuture<ValidateQueryResponse> validateQuery(IndicesAdminClient self,
                                                                       Closure requestClosure) {
        validateQueryAsync(self, requestClosure)
    }

    /**
     * Validate a query for correctness.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ValidateQueryRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ValidateQueryResponse> validateQueryAsync(IndicesAdminClient self,
                                                                            Closure requestClosure) {
        doRequestAsync(self, new ValidateQueryRequest(), requestClosure, self.&validateQuery)
    }

    /**
     * Put an index search warmer.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link PutWarmerRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#putWarmerAsync}.
     */
    @Deprecated
    static ListenableActionFuture<PutWarmerResponse> putWarmer(IndicesAdminClient self, Closure requestClosure) {
        putWarmerAsync(self, requestClosure)
    }

    /**
     * Put an index search warmer.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link PutWarmerRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<PutWarmerResponse> putWarmerAsync(IndicesAdminClient self, Closure requestClosure) {
        // warmer name is expected to be set by the closure
        doRequestAsync(self, new PutWarmerRequest(null), requestClosure, self.&putWarmer)
    }

    /**
     * Delete one or more index search warmers.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link DeleteWarmerRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#deleteWarmerAsync}.
     */
    @Deprecated
    static ListenableActionFuture<DeleteWarmerResponse> deleteWarmer(IndicesAdminClient self, Closure requestClosure) {
        deleteWarmerAsync(self, requestClosure)
    }

    /**
     * Delete one or more index search warmers.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link DeleteWarmerRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<DeleteWarmerResponse> deleteWarmerAsync(IndicesAdminClient self,
                                                                          Closure requestClosure) {
        doRequestAsync(self, new DeleteWarmerRequest(), requestClosure, self.&deleteWarmer)
    }

    /**
     * Get index search warmers.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetWarmersRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#getWarmersAsync}.
     */
    @Deprecated
    static ListenableActionFuture<GetWarmersResponse> getWarmers(IndicesAdminClient self, Closure requestClosure) {
        getWarmersAsync(self, requestClosure)
    }

    /**
     * Get index search warmers.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetWarmersRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetWarmersResponse> getWarmersAsync(IndicesAdminClient self, Closure requestClosure) {
        doRequestAsync(self, new GetWarmersRequest(), requestClosure, self.&getWarmers)
    }

    /**
     * Get index settings.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetSettingsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#getSettingsAsync}.
     */
    @Deprecated
    static ListenableActionFuture<GetSettingsResponse> getSettings(IndicesAdminClient self, Closure requestClosure) {
        getSettingsAsync(self, requestClosure)
    }

    /**
     * Get index settings.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetSettingsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetSettingsResponse> getSettingsAsync(IndicesAdminClient self,
                                                                        Closure requestClosure) {
        doRequestAsync(self, new GetSettingsRequest(), requestClosure, self.&getSettings)
    }

    /**
     * Analyze the {@code text} using the provided index.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link AnalyzeRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null} except {@code text}
     * @deprecated As of 1.5, replaced by {@link IndicesAdminClientExtensions#analyzeAsync}.
     */
    @Deprecated
    static ListenableActionFuture<AnalyzeResponse> analyze(IndicesAdminClient self,
                                                           String text,
                                                           Closure requestClosure) {
        analyzeAsync(self, text, requestClosure)
    }

    /**
     * Analyze the {@code text} using the provided index.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link AnalyzeRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null} except {@code text}
     */
    static ListenableActionFuture<AnalyzeResponse> analyzeAsync(IndicesAdminClient self,
                                                                String text,
                                                                Closure requestClosure) {
        // text must currently be supplied to the constructor
        doRequestAsync(self, new AnalyzeRequest(text), requestClosure, self.&analyze)
    }
}