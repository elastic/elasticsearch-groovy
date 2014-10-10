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
     */
    static ListenableActionFuture<RefreshResponse> refresh(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<RefreshRequest, RefreshResponse, IndicesAdminClient> wrapper =
                wrap(self, Requests.refreshRequest(), requestClosure)

        self.refresh(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Determine if the specified indices exist.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link IndicesExistsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<IndicesExistsResponse> exists(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<IndicesExistsRequest, IndicesExistsResponse, IndicesAdminClient> wrapper =
                wrap(self, Requests.indicesExistsRequest(), requestClosure)

        self.exists(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Determine if the specified types exist within the specified indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link TypesExistsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<TypesExistsResponse> typeExists(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<TypesExistsRequest, TypesExistsResponse, IndicesAdminClient> wrapper =
                wrap(self, new TypesExistsRequest(null), requestClosure)

        self.typesExists(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Get stats for indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link IndicesStatsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<IndicesStatsResponse> stats(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<IndicesStatsRequest, IndicesStatsResponse, IndicesAdminClient> wrapper =
                wrap(self, new IndicesStatsRequest(), requestClosure)

        self.stats(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Get details pertaining to the recovery state of indices and their associated shards.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link RecoveryRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<RecoveryResponse> recoveries(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<RecoveryRequest, RecoveryResponse, IndicesAdminClient> wrapper =
                wrap(self, new RecoveryRequest(), requestClosure)

        self.recoveries(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Get details pertaining to the segments of indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link IndicesSegmentsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<IndicesSegmentResponse> segments(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<IndicesSegmentsRequest, IndicesSegmentResponse, IndicesAdminClient> wrapper =
                wrap(self, Requests.indicesSegmentsRequest(), requestClosure)

        self.segments(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Create an index explicitly, which allows the index configuration to be specified.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link CreateIndexRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<CreateIndexResponse> create(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<CreateIndexRequest, CreateIndexResponse, IndicesAdminClient> wrapper =
                wrap(self, Requests.createIndexRequest(null), requestClosure)

        self.create(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
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
    static ListenableActionFuture<DeleteIndexResponse> delete(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<DeleteIndexRequest, DeleteIndexResponse, IndicesAdminClient> wrapper =
                wrap(self, Requests.deleteIndexRequest(null), requestClosure)

        self.delete(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
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
    static ListenableActionFuture<CloseIndexResponse> close(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<CloseIndexRequest, CloseIndexResponse, IndicesAdminClient> wrapper =
                wrap(self, Requests.closeIndexRequest(null), requestClosure)

        self.close(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Open the specified indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link OpenIndexRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<OpenIndexResponse> open(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<OpenIndexRequest, OpenIndexResponse, IndicesAdminClient> wrapper =
                wrap(self, Requests.openIndexRequest(null), requestClosure)

        self.open(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
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
    static ListenableActionFuture<FlushResponse> flush(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<FlushRequest, FlushResponse, IndicesAdminClient> wrapper =
                wrap(self, Requests.flushRequest(), requestClosure)

        self.flush(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
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
    static ListenableActionFuture<OptimizeResponse> optimize(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<OptimizeRequest, OptimizeResponse, IndicesAdminClient> wrapper =
                wrap(self, Requests.optimizeRequest(), requestClosure)

        self.optimize(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Get the mappings of one or more types.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetMappingsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetMappingsResponse> getMappings(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<GetMappingsRequest, GetMappingsResponse, IndicesAdminClient> wrapper =
                wrap(self, new GetMappingsRequest(), requestClosure)

        self.getMappings(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Get the mappings of one or more fields.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetFieldMappingsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetFieldMappingsResponse> getFieldMappings(IndicesAdminClient self,
                                                                             Closure requestClosure) {
        Wrapper<GetFieldMappingsRequest, GetFieldMappingsResponse, IndicesAdminClient> wrapper =
                wrap(self, new GetFieldMappingsRequest(), requestClosure)

        self.getFieldMappings(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Add the mapping definition for a type into one or more indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link PutMappingRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<PutMappingResponse> putMapping(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<PutMappingRequest, PutMappingResponse, IndicesAdminClient> wrapper =
                wrap(self, Requests.putMappingRequest(), requestClosure)

        self.putMapping(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Delete the mapping definition for a type in one or more indices.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link DeleteMappingRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<DeleteMappingResponse> deleteMapping(IndicesAdminClient self,
                                                                       Closure requestClosure) {
        Wrapper<DeleteMappingRequest, DeleteMappingResponse, IndicesAdminClient> wrapper =
                wrap(self, Requests.deleteMappingRequest(), requestClosure)

        self.deleteMapping(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Atomically add or remove index aliases.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link IndicesAliasesRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<IndicesAliasesResponse> aliases(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<IndicesAliasesRequest, IndicesAliasesResponse, IndicesAdminClient> wrapper =
                wrap(self, Requests.indexAliasesRequest(), requestClosure)

        self.aliases(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Get index aliases.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetAliasesRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetAliasesResponse> getAliases(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<GetAliasesRequest, GetAliasesResponse, IndicesAdminClient> wrapper =
                wrap(self, new GetAliasesRequest(), requestClosure)

        self.getAliases(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Determine if index aliases exist.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetAliasesRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<AliasesExistResponse> aliasesExist(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<GetAliasesRequest, AliasesExistResponse, IndicesAdminClient> wrapper =
                wrap(self, new GetAliasesRequest(), requestClosure)

        self.aliasesExist(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Clear the indices specified caches.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ClearIndicesCacheRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ClearIndicesCacheResponse> clearCache(IndicesAdminClient self,
                                                                        Closure requestClosure) {
        Wrapper<ClearIndicesCacheRequest, ClearIndicesCacheResponse, IndicesAdminClient> wrapper =
                wrap(self, Requests.clearIndicesCacheRequest(), requestClosure)

        self.clearCache(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
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
    static ListenableActionFuture<UpdateSettingsResponse> updateSettings(IndicesAdminClient self,
                                                                         Closure requestClosure) {
        Wrapper<UpdateSettingsRequest, UpdateSettingsResponse, IndicesAdminClient> wrapper =
                wrap(self, new UpdateSettingsRequest(), requestClosure)

        self.updateSettings(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Add an index template to enable automatic type mappings.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link PutIndexTemplateRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<PutIndexTemplateResponse> putTemplate(IndicesAdminClient self,
                                                                        Closure requestClosure) {
        Wrapper<PutIndexTemplateRequest, PutIndexTemplateResponse, IndicesAdminClient> wrapper =
                wrap(self, new PutIndexTemplateRequest(null), requestClosure)

        self.putTemplate(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
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
    static ListenableActionFuture<DeleteIndexTemplateResponse> deleteTemplate(IndicesAdminClient self, String name) {
        Wrapper<DeleteIndexTemplateRequest, DeleteIndexTemplateResponse, IndicesAdminClient> wrapper =
                wrap(self, new DeleteIndexTemplateRequest(name))

        self.deleteTemplate(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Get index templates.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetIndexTemplatesRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetIndexTemplatesResponse> getTemplates(IndicesAdminClient self,
                                                                          Closure requestClosure) {
        Wrapper<GetIndexTemplatesRequest, GetIndexTemplatesResponse, IndicesAdminClient> wrapper =
                wrap(self, new GetIndexTemplatesRequest(), requestClosure)

        self.getTemplates(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Validate a query for correctness.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link ValidateQueryRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<ValidateQueryResponse> validateQuery(IndicesAdminClient self,
                                                                       Closure requestClosure) {
        Wrapper<ValidateQueryRequest, ValidateQueryResponse, IndicesAdminClient> wrapper =
                wrap(self, new ValidateQueryRequest(), requestClosure)

        self.validateQuery(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Put an index search warmer.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link PutWarmerRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<PutWarmerResponse> putWarmer(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<PutWarmerRequest, PutWarmerResponse, IndicesAdminClient> wrapper =
                wrap(self, new PutWarmerRequest(null), requestClosure)

        self.putWarmer(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Delete one or more index search warmers.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link DeleteWarmerRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<DeleteWarmerResponse> deleteWarmer(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<DeleteWarmerRequest, DeleteWarmerResponse, IndicesAdminClient> wrapper =
                wrap(self, new DeleteWarmerRequest(), requestClosure)

        self.deleteWarmer(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Get index search warmers.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetWarmersRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetWarmersResponse> getWarmers(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<GetWarmersRequest, GetWarmersResponse, IndicesAdminClient> wrapper =
                wrap(self, new GetWarmersRequest(), requestClosure)

        self.getWarmers(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Get index settings.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link GetSettingsRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static ListenableActionFuture<GetSettingsResponse> getSettings(IndicesAdminClient self, Closure requestClosure) {
        Wrapper<GetSettingsRequest, GetSettingsResponse, IndicesAdminClient> wrapper =
                wrap(self, new GetSettingsRequest(), requestClosure)

        self.getSettings(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }

    /**
     * Analyze the {@code text} using the provided index.
     *
     * @param self The {@code this} reference for the {@link IndicesAdminClient}.
     * @param requestClosure The map-like closure that configures the {@link AnalyzeRequest}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null} except {@code text}
     */
    static ListenableActionFuture<AnalyzeResponse> analyze(IndicesAdminClient self,
                                                           String text,
                                                           Closure requestClosure) {
        Wrapper<AnalyzeRequest, AnalyzeResponse, IndicesAdminClient> wrapper =
                wrap(self, new AnalyzeRequest(text), requestClosure)

        self.analyze(wrapper.request, wrapper.responseFuture)

        wrapper.responseFuture
    }
}