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
import org.elasticsearch.action.explain.ExplainResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.get.MultiGetResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.indexedscripts.delete.DeleteIndexedScriptResponse
import org.elasticsearch.action.indexedscripts.get.GetIndexedScriptResponse
import org.elasticsearch.action.indexedscripts.put.PutIndexedScriptResponse
import org.elasticsearch.action.search.ClearScrollResponse
import org.elasticsearch.action.search.MultiSearchResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.action.update.UpdateResponse
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
        }

        assert response.index == indexName
        assert response.type == 'tweet'
        assert response.id == tweetId
    }

    @Test
    void testIndexRequestAsync() {
        String tweetId = randomInt()

        IndexResponse response = client.indexAsync {
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

        // don't need a refresh, since we're not searching for it
        DeleteResponse response = client.delete {
            index indexName
            type typeName
            id docId
        }

        assert response.found
        assert response.id == docId

        // don't need a refresh, since we're not searching for it
        GetResponse getResponse = client.get {
            index indexName
            type typeName
            id docId
        }

        assert ! getResponse.exists
    }

    @Test
    void testDeleteRequestAsync() {
        String docId = indexDoc(indexName, typeName) {
            user = randomAsciiOfLengthBetween(1, 16)
        }

        // don't need a refresh, since we're not searching for it
        DeleteResponse response = client.deleteAsync {
            index indexName
            type typeName
            id docId
        }.actionGet()

        assert response.found
        assert response.id == docId

        // don't need a refresh, since we're not searching for it
        GetResponse getResponse = client.getAsync {
            index indexName
            type typeName
            id docId
        }.actionGet()

        assert ! getResponse.exists
    }

    @Test
    void testUpdateRequest() {
        String userId = randomAsciiOfLengthBetween(1, 16)

        int nestedValue = randomInt()

        String docId = indexDoc(indexName, typeName) {
            user = userId
        }

        // update the document by adding a nested object (creatively named "nested")
        UpdateResponse response = client.update {
            index indexName
            type typeName
            id docId
            doc {
                nested {
                    value = nestedValue
                }
            }
        }

        assert ! response.created
        assert response.version == 2

        // don't need a refresh, since we're not searching for it
        GetResponse getResponse = client.get {
            index indexName
            type typeName
            id docId
        }

        assert getResponse.exists
        assert getResponse.version == 2
        assert getResponse.sourceAsMap["user"] == userId
        assert getResponse.sourceAsMap.nested.value == nestedValue
    }

    @Test
    void testUpdateRequestAsync() {
        String userId = randomAsciiOfLengthBetween(1, 16)

        int nestedValue = randomInt()

        String docId = indexDoc(indexName, typeName) {
            user = userId
        }

        // update the document by adding a nested object (creatively named "nested")
        UpdateResponse response = client.updateAsync {
            index indexName
            type typeName
            id docId
            doc {
                nested {
                    value = nestedValue
                }
            }
        }.actionGet()

        assert ! response.created
        assert response.version == 2

        // don't need a refresh, since we're not searching for it
        GetResponse getResponse = client.getAsync {
            index indexName
            type typeName
            id docId
        }.actionGet()

        assert getResponse.exists
        assert getResponse.version == 2
        assert getResponse.sourceAsMap["user"] == userId
        assert getResponse.sourceAsMap.nested.value == nestedValue
    }

    @Test
    void testGetRequest() {
        String userId = randomAsciiOfLengthBetween(1, 16)

        String docId = indexDoc(indexName, typeName) {
            user = userId
        }

        // don't need a refresh, since we're not searching for it
        GetResponse response = client.get {
            index indexName
            type typeName
            id docId
        }

        assert response.exists
        assert response.id == docId
        assert response.sourceAsMap["user"] == userId
    }

    @Test
    void testGetRequestAsync() {
        String userId = randomAsciiOfLengthBetween(1, 16)

        String docId = indexDoc(indexName, typeName) {
            user = userId
        }

        // don't need a refresh, since we're not searching for it
        GetResponse response = client.getAsync {
            index indexName
            type typeName
            id docId
        }.actionGet()

        assert response.exists
        assert response.id == docId
        assert response.sourceAsMap["user"] == userId
    }

    @Test
    void testMultiGetRequest() {
        String userId1 = randomAsciiOfLengthBetween(1, 4)
        String userId2 = randomAsciiOfLengthBetween(5, 8)
        String userId3 = randomAsciiOfLengthBetween(9, 12)

        // Three separate index operations
        BulkResponse bulkResponse = bulkIndex(indexName, [
            {
                type typeName
                source { user = userId1 }
            },
            {
                type typeName
                source {
                    user = userId2
                    field = randomInt()
                }
            },
            {
                type typeName
                source { user = userId3 }
            }
        ])

        // don't need a refresh, since we're not searching for it
        MultiGetResponse response = client.multiGet {
            for (BulkItemResponse bulkItemResponse : bulkResponse.items) {
                add indexName, typeName, bulkItemResponse.id
            }
        }

        assert response.responses.length == 3
        assert response.responses[0].response.sourceAsMap.user == userId1
        assert response.responses[1].response.sourceAsMap.user == userId2
        assert response.responses[2].response.sourceAsMap.user == userId3
    }

    @Test
    void testMultiGetRequestAsync() {
        String userId1 = randomAsciiOfLengthBetween(1, 4)
        String userId2 = randomAsciiOfLengthBetween(5, 8)
        String userId3 = randomAsciiOfLengthBetween(9, 12)

        // Three separate index operations
        BulkResponse bulkResponse = bulkIndex(indexName, [
            {
                type typeName
                source { user = userId1 }
            },
            {
                type typeName
                source {
                    user = userId2
                    field = randomInt()
                }
            },
            {
                type typeName
                source { user = userId3 }
            }
        ])

        // don't need a refresh, since we're not searching for it
        MultiGetResponse response = client.multiGetAsync {
            for (BulkItemResponse bulkItemResponse : bulkResponse.items) {
                add indexName, typeName, bulkItemResponse.id
            }
        }.actionGet()

        assert response.responses.length == 3
        assert response.responses[0].response.sourceAsMap.user == userId1
        assert response.responses[1].response.sourceAsMap.user == userId2
        assert response.responses[2].response.sourceAsMap.user == userId3
    }

    @Test
    void testBulkRequest() {
        List<String> ids = [randomAsciiOfLength(1), randomAsciiOfLength(2), randomAsciiOfLength(3)]

        BulkResponse response = client.bulk {
            // Note: this uses add(ActionRequest...) [note the comma between each request]
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
        }

        assert ! response.hasFailures()
        assert response.items.length == 3
        // ensure each item was indexed as expected
        response.items.eachWithIndex { BulkItemResponse item, int i ->
            assert item.index == indexName
            assert item.type == typeName
            assert item.id == ids[i]
        }
    }

    @Test
    void testBulkRequestAsync() {
        List<String> ids = [randomAsciiOfLength(1), randomAsciiOfLength(2), randomAsciiOfLength(3)]

        BulkResponse response = client.bulkAsync {
            // Note: this uses add(ActionRequest...) [note the comma between each request]
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
        assert response.items.length == 3
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
        client.admin.indices.refresh { indices indexName }

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
        }

        assert response.hits.totalHits == 1
        assert response.hits.hits[0].id == docId
        assert response.hits.hits[0].source.user == userId
    }

    @Test
    void testSearchRequestAsync() {
        // int used for ID to ensure that the search is simple
        String userId = randomInt()

        String docId = indexDoc(indexName, typeName) {
            user = userId
        }

        // refresh the index to guarantee searchability
        client.admin.indices.refreshAsync { indices indexName }.actionGet()

        SearchResponse response = client.searchAsync {
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
        assert response.hits.hits[0].source.user == userId
    }

    @Test
    void testMultiSearchRequest() {
        List<Integer> values = bulkIndexValues()
        // the value that all must be greater than or equal to in the first search; less than in the second search
        int gteValue = values[randomInt(values.size() - 1)]

        // determine how many indexed documents have a value >= gteValue and, separately value < gteValue
        MultiSearchResponse response = client.multiSearch {
            add Requests.searchRequest(indexName).types(typeName).source {
                query {
                    range {
                        value {
                            gte = gteValue
                        }
                    }
                }
            }
            add Requests.searchRequest().with {
                indices indexName
                types typeName
                source {
                    query {
                        range {
                            value {
                                lt = gteValue
                            }
                        }
                    }
                }
            }
        }

        // the counts should match exactly since they're doing the same operation
        assert response.responses[0].response.hits.totalHits == values.count { it >= gteValue }
        assert response.responses[1].response.hits.totalHits == values.count { it < gteValue }
    }

    @Test
    void testMultiSearchRequestAsync() {
        List<Integer> values = bulkIndexValues()
        // the value that all must be greater than or equal to in the first search; less than in the second search
        int gteValue = values[randomInt(values.size() - 1)]

        // determine how many indexed documents have a value >= gteValue and, separately value < gteValue
        MultiSearchResponse response = client.multiSearchAsync {
            add Requests.searchRequest(indexName).types(typeName).source {
                query {
                    range {
                        value {
                            gte = gteValue
                        }
                    }
                }
            }
            add Requests.searchRequest().with {
                indices indexName
                types typeName
                source {
                    query {
                        range {
                            value {
                                lt = gteValue
                            }
                        }
                    }
                }
            }
        }.actionGet()

        // the counts should match exactly since they're doing the same operation
        assert response.responses[0].response.hits.totalHits == values.count { it >= gteValue }
        assert response.responses[1].response.hits.totalHits == values.count { it < gteValue }
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
        }

        // the counts should match exactly since they're doing the same operation
        assert response.count == values.count { it >= gteValue }
    }

    @Test
    void testCountRequestAsync() {
        List<Integer> values = bulkIndexValues()
        // the value that all must be greater than or equal to
        int gteValue = values[randomInt(values.size() - 1)]

        // determine how many indexed documents have a value >= gteValue
        CountResponse response = client.countAsync {
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
    void testSearchScrollRequest() {
        // index some arbitrary data
        List<Integer> values =  bulkIndexValues()

        // Open a new scroll ID
        SearchResponse searchResponse = client.search {
            source {
                query {
                    match_all { }
                }
                // Note: Size is per relevant shard!
                size 1000
            }
            searchType SearchType.SCAN
            scroll "60s"
        }

        // sanity check
        assert searchResponse.hits.totalHits == values.size()
        assert searchResponse.scrollId != null
        assert searchResponse.hits.hits.length == 0

        SearchResponse response = client.searchScroll {
            scrollId searchResponse.scrollId
            // keep the next window open
            scroll "60s"
        }

        // Cleanup any open/locked resources
        ClearScrollResponse clearResponse = client.clearScroll {
            // NOTE: The response contains the _next_ scroll ID
            addScrollId response.scrollId
        }

        // Ensure that it was successful
        assert clearResponse.succeeded
    }

    @Test
    void testSearchScrollRequestAsync() {
        // index some arbitrary data
        List<Integer> values =  bulkIndexValues()

        // Open a new scroll ID
        SearchResponse searchResponse = client.searchAsync {
            source {
                query {
                    match_all { }
                }
                // Note: Size is per relevant shard!
                size 1000
            }
            searchType SearchType.SCAN
            scroll "60s"
        }.actionGet()

        // sanity check
        assert searchResponse.hits.totalHits == values.size()
        assert searchResponse.scrollId != null
        assert searchResponse.hits.hits.length == 0

        SearchResponse response = client.searchScrollAsync {
            scrollId searchResponse.scrollId
            // keep the next window open
            scroll "60s"
        }.actionGet()

        // Cleanup any open/locked resources
        ClearScrollResponse clearResponse = client.clearScrollAsync {
            // NOTE: The response contains the _next_ scroll ID
            addScrollId response.scrollId
        }.actionGet()

        // Ensure that it was successful
        assert clearResponse.succeeded
    }

    @Test
    void testClearScrollRequest() {
        // index some arbitrary data
        List<Integer> values =  bulkIndexValues()

        // Open a new scroll ID
        SearchResponse searchResponse = client.search {
            source {
                query {
                    match_all { }
                }
                // Note: Size is per relevant shard!
                size 1000
            }
            searchType SearchType.SCAN
            scroll "60s"
        }

        // sanity check
        assert searchResponse.hits.totalHits == values.size()
        assert searchResponse.scrollId != null

        // Cleanup any open/locked resources
        ClearScrollResponse response = client.clearScroll {
            addScrollId searchResponse.scrollId
        }

        // Ensure that it was successful
        assert response.succeeded
    }

    @Test
    void testClearScrollRequestAsync() {
        // index some arbitrary data
        List<Integer> values =  bulkIndexValues()

        // Open a new scroll ID
        SearchResponse searchResponse = client.searchAsync {
            source {
                query {
                    match_all { }
                }
                // Note: Size is per relevant shard!
                size 1000
            }
            searchType SearchType.SCAN
            scroll "60s"
        }.actionGet()

        // sanity check
        assert searchResponse.hits.totalHits == values.size()
        assert searchResponse.scrollId != null

        // Cleanup any open/locked resources
        ClearScrollResponse response = client.clearScrollAsync {
            addScrollId searchResponse.scrollId
        }.actionGet()

        // Ensure that it was successful
        assert response.succeeded
    }

    @Test
    void testPutIndexedScriptRequest() {
        int startCount = randomInt(1024)

        String docId = indexDoc(indexName, typeName) {
            // actual value is ignored
            user = randomAsciiOfLengthBetween(1, 16)
            count = startCount
        }

        // index the script
        PutIndexedScriptResponse response = client.putIndexedScript {
            id 'testPutIndexedScriptRequest'
            // NOTE: This will be the Groovy runtime within Elasticsearch and not the Groovy client (this)
            scriptLang 'groovy'
            source {
                // NOTE: The script is [in this case] Groovy, but it must be a string that is interpreted on the server
                script = "ctx._source.count += count"
            }
        }

        assert response.created

        UpdateResponse updateResponse = client.update {
            index indexName
            type typeName
            id docId
            source {
                script_id 'testPutIndexedScriptRequest'
                lang 'groovy'
                params {
                    count = 5
                }
            }
        }

        assert ! updateResponse.created
        assert updateResponse.id == docId
        assert updateResponse.version == 2

        GetResponse getResponse = client.get {
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
    void testPutIndexedScriptRequestAsync() {
        int startCount = randomInt(1024)

        String docId = indexDoc(indexName, typeName) {
            // actual value is ignored
            user = randomAsciiOfLengthBetween(1, 16)
            count = startCount
        }

        // index the script
        PutIndexedScriptResponse response = client.putIndexedScriptAsync {
            id 'testPutIndexedScriptRequestAsync'
            // NOTE: This will be the Groovy runtime within Elasticsearch and not the Groovy client (this)
            scriptLang 'groovy'
            source {
                // NOTE: The script is [in this case] Groovy, but it must be a string that is interpreted on the server
                script = "ctx._source.count += count"
            }
        }.actionGet()

        assert response.created

        UpdateResponse updateResponse = client.updateAsync {
            index indexName
            type typeName
            id docId
            source {
                script_id 'testPutIndexedScriptRequestAsync'
                lang 'groovy'
                params {
                    count = 5
                }
            }
        }.actionGet()

        assert ! updateResponse.created
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
    void testGetIndexedScriptRequest() {
        String groovyScript = "ctx._source.count += count"

        // index the script
        PutIndexedScriptResponse putResponse = client.putIndexedScript {
            id 'testGetIndexedScriptRequest'
            // NOTE: This will be the Groovy runtime within Elasticsearch and not the Groovy client (this)
            scriptLang 'groovy'
            source {
                // NOTE: The script is [in this case] Groovy, but it must be a string that is interpreted on the server
                script = groovyScript
            }
        }

        assert putResponse.created

        GetIndexedScriptResponse response = client.getIndexedScript {
            id 'testGetIndexedScriptRequest'
            scriptLang 'groovy'
        }

        assert response.exists
        assert response.version == putResponse.version
        assert response.script == groovyScript
    }

    @Test
    void testGetIndexedScriptRequestAsync() {
        String groovyScript = "ctx._source.count += count"

        // index the script
        PutIndexedScriptResponse putResponse = client.putIndexedScriptAsync {
            id 'testGetIndexedScriptRequestAsync'
            // NOTE: This will be the Groovy runtime within Elasticsearch and not the Groovy client (this)
            scriptLang 'groovy'
            source {
                // NOTE: The script is [in this case] Groovy, but it must be a string that is interpreted on the server
                script = groovyScript
            }
        }.actionGet()

        assert putResponse.created

        GetIndexedScriptResponse response = client.getIndexedScriptAsync {
            id 'testGetIndexedScriptRequestAsync'
            scriptLang 'groovy'
        }.actionGet()

        assert response.exists
        assert response.version == putResponse.version
        assert response.script == groovyScript
    }

    @Test
    void testDeleteIndexedScriptRequest() {
        String groovyScript = "ctx._source.count += count"

        // index the script
        PutIndexedScriptResponse putResponse = client.putIndexedScript {
            id 'testDeleteIndexedScriptRequest'
            scriptLang 'groovy'
            source {
                script = "ctx._source.count += count"
            }
        }

        assert putResponse.created

        // perform the delete
        DeleteIndexedScriptResponse response = client.deleteIndexedScript {
            id 'testDeleteIndexedScriptRequest'
            scriptLang 'groovy'
        }

        assert response.found

        GetIndexedScriptResponse getResponse = client.getIndexedScript {
            id 'testDeleteIndexedScriptRequest'
            scriptLang 'groovy'
        }

        assert ! getResponse.exists
    }

    @Test
    void testDeleteIndexedScriptRequestAsync() {
        String groovyScript = "ctx._source.count += count"

        // index the script
        PutIndexedScriptResponse putResponse = client.putIndexedScriptAsync {
            id 'testDeleteIndexedScriptRequestAsync'
            scriptLang 'groovy'
            source {
                script = "ctx._source.count += count"
            }
        }.actionGet()

        assert putResponse.created

        // perform the delete
        DeleteIndexedScriptResponse response = client.deleteIndexedScriptAsync {
            id 'testDeleteIndexedScriptRequestAsync'
            scriptLang 'groovy'
        }.actionGet()

        assert response.found

        GetIndexedScriptResponse getResponse = client.getIndexedScriptAsync {
            id 'testDeleteIndexedScriptRequestAsync'
            scriptLang 'groovy'
        }.actionGet()

        assert ! getResponse.exists
    }

    @Test
    void testExplainRequest() {
        int matchInteger = randomInt()

        // send in a document to match
        String docId = indexDoc(indexName, typeName) {
            value = matchInteger
        }

        // refresh the index to guarantee searchability
        assert client.admin.indices.refresh { indices indexName }.failedShards == 0

        // Reference the document that we're interested in (just created) to see if we can find it
        ExplainResponse response = client.explain {
            index indexName
            type typeName
            id docId
            source {
                query {
                    match {
                        value = matchInteger
                    }
                }
            }
        }

        // If it matched, then we know that explain works
        assert response.match
    }

    @Test
    void testExplainRequestAsync() {
        int matchInteger = randomInt()

        // send in a document to match
        String docId = indexDoc(indexName, typeName) {
            value = matchInteger
        }

        // refresh the index to guarantee searchability
        assert client.admin.indices.refresh { indices indexName }.failedShards == 0

        // Reference the document that we're interested in (just created) to see if we can find it
        ExplainResponse response = client.explainAsync {
            index indexName
            type typeName
            id docId
            source {
                query {
                    match {
                        value = matchInteger
                    }
                }
            }
        }.actionGet()

        // If it matched, then we know that explain works
        assert response.match
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

        // refresh the index to guarantee searchability
        assert client.admin.indices.refresh { indices indexName }.failedShards == 0

        values
    }
}