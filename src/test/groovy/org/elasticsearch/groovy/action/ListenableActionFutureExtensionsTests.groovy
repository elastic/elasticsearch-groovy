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
package org.elasticsearch.groovy.action

import org.elasticsearch.ElasticsearchException
import org.elasticsearch.action.ListenableActionFuture
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.test.ElasticsearchIntegrationTest

import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout

import java.util.concurrent.CountDownLatch

/**
 * Tests {@link ListenableActionFutureExtensions}.
 */
class ListenableActionFutureExtensionsTests extends ElasticsearchIntegrationTest {
    /**
     * Timeout for each <em>individual</em> test (and non-static to avoid wasted test resources).
     */
    @Rule
    public Timeout timeout = new Timeout(180000)

    /**
     * The index to use for most tests.
     */
    String indexName = 'laf'
    /**
     * The index type to use for most tests.
     */
    String typeName = 'listeners'
    /**
     * The document ID used for most tests.
     */
    String docId = '1'

    @Test
    void testListener_success() {
        IndexResponse successResponse = null

        // wait until both callbacks have been invoked (also the reason for the timeout)
        CountDownLatch latch = new CountDownLatch(1)

        // arbitrary action performed to get a response
        ListenableActionFuture<IndexResponse> future = indexRequestSuccess()

        // generic listener receives both success and failure messages:
        assert ListenableActionFutureExtensions.listener(future) { response, e ->
            assert response != null
            assert e == null

            // remember the response
            successResponse = response

            // mark invocation #1
            latch.countDown()
        } == future

        IndexResponse response = future.actionGet()

        // wait for the listener to be invoked
        latch.await()

        // ensure it got the same response
        assert response == successResponse
    }

    @Test
    void testListener_failure() {
        Throwable failureException = null

        // wait until both callbacks have been invoked (also the reason for the timeout)
        CountDownLatch latch = new CountDownLatch(1)

        // arbitrary action performed to get a response
        ListenableActionFuture<UpdateResponse> future = updateRequestFailure()

        // generic listener receives both success and failure messages:
        assert ListenableActionFutureExtensions.listener(future) { response, e ->
            assert response == null
            assert e != null

            // remember the failure
            failureException = e

            // mark invocation #1
            latch.countDown()
        } == future

        try {
            // expected to fail
            future.actionGet()

            fail("The request should fail.")
        }
        catch (ElasticsearchException expected) {
            // wait for the listener to be invoked
            latch.await()

            assert expected == failureException
        }
    }

    @Test
    void testSuccessListener() {
        IndexResponse successResponse = null

        // wait until both callbacks have been invoked (also the reason for the timeout)
        CountDownLatch latch = new CountDownLatch(1)

        // arbitrary action performed to get a response
        ListenableActionFuture<IndexResponse> future = indexRequestSuccess()

        // generic listener receives both success and failure messages:
        assert ListenableActionFutureExtensions.successListener(future) {
            assert it != null

            // remember the response
            successResponse = it

            // mark invocation #1
            latch.countDown()
        } == future

        IndexResponse response = future.actionGet()

        // wait for the listeners to be invoked
        latch.await()

        assert response == successResponse
    }

    @Test
    void testFailureListener() {
        Throwable failureException = null

        // wait until both callbacks have been invoked (also the reason for the timeout)
        CountDownLatch latch = new CountDownLatch(1)

        // arbitrary action performed to get a response
        ListenableActionFuture<UpdateResponse> future = updateRequestFailure()

        // generic listener receives both success and failure messages:
        assert ListenableActionFutureExtensions.failureListener(future) {
            assert it != null

            // remember the failure
            failureException = it

            // mark invocation #1
            latch.countDown()
        } == future

        try {
            // expected to fail
            future.actionGet()

            fail("The request should fail.")
        }
        catch (ElasticsearchException expected) {
            // wait for the listener to be invoked
            latch.await()

            assert expected == failureException
        }
    }

    @Test
    void testAllProperlyExecuted() {
        // wait until both callbacks have been invoked (also the reason for the timeout)
        CountDownLatch latch = new CountDownLatch(3)

        // responses coming from the listeners
        IndexResponse responseFromListener = null
        IndexResponse responseFromSuccess1 = null
        IndexResponse responseFromSuccess2 = null

        // arbitrary action performed to get a response
        ListenableActionFuture<IndexResponse> future = indexRequestSuccess()

        // generic listener receives both success and failure messages:
        ListenableActionFutureExtensions.listener(future) { response, e ->
            responseFromListener = response

            // mark invocation #1
            latch.countDown()
        }

        // success listener only receives invocations from successful calls:
        ListenableActionFutureExtensions.successListener(future) {
            responseFromSuccess1 = it

            // mark invocation #2
            latch.countDown()
        }

        // success listener only receives invocations from successful calls (note: this is the second success listener
        //  and both are expected to be invoked):
        ListenableActionFutureExtensions.successListener(future) {
            responseFromSuccess2 = it

            // mark invocation #3
            latch.countDown()
        }

        // failure listener only receives invocations from failed calls:
        ListenableActionFutureExtensions.failureListener(future) {
            fail("This should never be invoked.")
        }

        // run the future
        IndexResponse response = future.actionGet()

        // give the listeners the chance to be invoked
        latch.await()

        // all should be the same:
        assert response == responseFromListener
        assert response == responseFromSuccess1
        assert response == responseFromSuccess2
    }

    @Test
    void testExtensionModuleConfigured() {
        CountDownLatch latch = new CountDownLatch(4)

        ListenableActionFuture<IndexResponse> successfulFuture = indexRequestSuccess()
        // note: this breaks because the mapping is invalid after running successfulFuture
        ListenableActionFuture<UpdateResponse> failedFuture = updateRequestFailure()

        // NOTE: Setting up to see success (with asserts) and failure (without)
        // Only checking the result for one of the listeners, then chaining the other

        // should return itself for chaining
        assert successfulFuture.listener { IndexResponse response, Throwable e ->
            if (response == null) {
                fail("No response.")
            }
            else if (e != null) {
                fail("Should never fail.")
            }

            // mark successful invocation #1
            latch.countDown()
        } == successfulFuture

        assert successfulFuture.successListener {
            if (it == null) {
                fail("No response to successful listener.")
            }

            // mark successful invocation #2
            latch.countDown()
        } == successfulFuture

        assert successfulFuture.failureListener {
            fail("Should never fail, but failure listener invoked.")
        } == successfulFuture

        // now, chaining failure calls:
        failedFuture.listener { response, e ->
            if (response != null) {
                fail("Should not be successful.")
            }
            else if (e == null) {
                fail("Should always fail, but no exception was passed in.")
            }

            // mark failed invocation #1
            latch.countDown()
        }.successListener {
            fail("Should always fail, but successful listener invoked.")
        }.failureListener {
            if (it == null) {
                fail("No exception was passed in.")
            }

            // mark failed invocation #1
            latch.countDown()
        }

        try {
            assert successfulFuture.actionGet().created
            failedFuture.actionGet()

            fail("Expected failure.")
        }
        catch (ElasticsearchException expected) {
            // expected exception; now ensure that the callbacks are called
            latch.await()
        }
    }

    /**
     * Prepare an {@code IndexRequest} that contains a document with a {@code user} field.
     * <p />
     * This request should always succeed.
     *
     * @return Never {@code null}.
     */
    private ListenableActionFuture<IndexResponse> indexRequestSuccess() {
        client().index {
            index indexName
            type typeName
            id docId
            source {
                user = randomAsciiOfLength(2)
            }
        }
    }


    /**
     * Prepare an {@code UpdateRequest} that uses a non-existent script from a non-existent language.
     * <p />
     * This request should always fail.
     *
     * @return Never {@code null}.
     */
    private ListenableActionFuture<UpdateResponse> updateRequestFailure() {
        client().update {
            index indexName
            type typeName
            id docId
            source {
                script_id 'does_not_exist'
                lang 'does-not-exist'
            }
        }
    }


}
