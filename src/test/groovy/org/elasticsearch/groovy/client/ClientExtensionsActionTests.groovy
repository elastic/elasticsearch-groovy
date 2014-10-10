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

import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchResponse

import org.junit.Test

/**
 * Tests {@code ActionRequest}s added by {@link ClientExtensions}.
 * <p />
 * This test assumes that the admin client extensions are applied as well.
 */
class ClientExtensionsActionTests extends AbstractClientTests {
    /**
     * The index to use for most tests.
     */
    String indexName = 'client'
    /**
     * The index type to use for most tests.
     */
    String typeName = 'actions'

    @Test
    void testIndexRequest() {
        String tweetId = randomInt()

        IndexResponse response = client.index {
            index indexName
            type 'tweet'
            id tweetId
            source toIndexBytes {
                user = "kimchy"
                message = "this is a tweet!"
            }
        }.actionGet()

        assert response.index == indexName
        assert response.type == 'tweet'
        assert response.id == tweetId
    }

    @Test
    void testDeleteRequest() {
        String docId = indexDoc(indexName, typeName) {
            user = randomAsciiOfLengthBetween(1, 16)
        }

        // don't need a refresh, since we're not searching it
        DeleteResponse response = client.delete {
            index indexName
            type typeName
            id docId
        }.actionGet()

        assert response.found
        assert response.id == docId
    }

    @Test
    void testGetRequest() {
        String userId = randomAsciiOfLengthBetween(1, 16)

        String docId = indexDoc(indexName, typeName) {
            user = userId
        }

        // don't need a refresh, since we're not searching it
        GetResponse response = client.get {
            index indexName
            type typeName
            id docId
        }.actionGet()

        assert response.exists
        assert response.id == docId
        assert response.sourceAsMap["user"] == userId
    }

    @Test
    void testSearchRequest() {
        // int used for ID to ensure that the search is simple
        String userId = randomInt()

        String docId = indexDoc(indexName, typeName) {
            user = userId
        }

        // refresh the index to guarantee searchability
        client.admin.indices.refresh { indices indexName }.actionGet()

        SearchResponse response = client.search {
            indices indexName
            types typeName
            source toBytes {
                query {
                    match {
                        user = userId
                    }
                }
            }
        }.actionGet()

        assert response.hits.totalHits == 1
        assert response.hits.hits[0].id == docId
        assert response.hits.hits[0].source["user"] == userId
    }
}