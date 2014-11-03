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

import org.elasticsearch.action.bulk.BulkItemResponse
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.count.CountResponse
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.Requests
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
            source {
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
    void testBulkRequest() {
        List<String> ids = [randomAsciiOfLength(1), randomAsciiOfLength(2), randomAsciiOfLength(3)]

        BulkResponse response = client.bulk {
            // note: this uses add(ActionRequest...) [note the comma between each request]
            add Requests.indexRequest(indexName).with {
                type typeName
                id ids[0]
                source {
                    user = randomInt()
                }
            },
            Requests.indexRequest(indexName).with {
                type typeName
                id ids[1]
                source {
                    user = randomInt()
                }
            },
            Requests.indexRequest(indexName).with {
                type typeName
                id ids[2]
                source {
                    user = randomInt()
                }
            }
        }.actionGet()

        assert ! response.hasFailures()
        // ensure each item was indexed as expected
        response.items.eachWithIndex { BulkItemResponse item, int i ->
            assert item.index == indexName
            assert item.type == typeName
            assert item.id == ids[i]
        }
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
            source {
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

    @Test
    void testCountRequest() {
        List<Integer> values = bulkIndexValues()
        // the value that all must be greater than or equal to
        int gteValue = values[randomInt(values.size() - 1)]

        // determine how many indexed documents have a value >= gteValue
        CountResponse response = client.count {
            indices indexName
            types typeName
            source {
                query {
                    range {
                        value {
                            gte = gteValue
                        }
                    }
                }
            }
        }.actionGet()

        // the counts should match exactly since they're doing the same operation
        assert response.count == values.count { it >= gteValue }
    }

    @Test
    void testDeleteByQueryRequest() {
        List<Integer> values = bulkIndexValues()
        // the value that all must be greater than or equal to
        int gteValue = values[randomInt(values.size() - 1)]

        DeleteByQueryResponse response = client.deleteByQuery {
            indices indexName
            types typeName
            source {
                query {
                    range {
                        value {
                            gte = gteValue
                        }
                    }
                }
            }
        }.actionGet()

        // sanity check to ensure that we didn't screw up
        assert response.indices[indexName].failedShards == 0

        // guarantee that the deletes take effect
        assert client.admin.indices.refresh { indices indexName }.actionGet().failedShards == 0

        // determine how many indexed documents have a value >= gteValue
        CountResponse countResponse = client.count {
            indices indexName
            types typeName
            source {
                query {
                    match_all { }
                }
            }
        }.actionGet()

        // the counts should match exactly since we have the whole data set and are performing the opposite calculation
        assert countResponse.count == values.count { it < gteValue }
    }

    /**
     * Bulk index a random number of documents containing a random integer field named "value" at its root.
     * <p />
     * Document are guaranteed to exist for each value in the {@link List}, but values are not guaranteed to be unique.
     *
     * @return Never {@code null}. Never {@link List#isEmpty() empty}. The size of the resulting {@link List} is
     *         guaranteed to be the same as the number of indexed documents, even if values are duplicated, which is
     *         possible.
     */
    List<Integer> bulkIndexValues() {
        int size = scaledRandomIntBetween(3, 10)
        List<Integer> values = []

        // build the arrays
        for (int i = 0; i < size; ++i) {
            values.add(randomInt())
        }

        // index all of the values
        bulkIndex(indexName, values.collect { int currentValue ->
            // explicit return for compiler to identify otherwise ambiguous code block
            return {
                type typeName
                source {
                    // note: "it" cannot be used because it's overridden (and always null)
                    value = currentValue
                }
            }
        })

        values
    }
}