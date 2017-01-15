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
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.ActionRequest
import org.elasticsearch.action.ActionRequestValidationException
import org.elasticsearch.action.ActionResponse
import org.elasticsearch.action.ListenableActionFuture
import org.elasticsearch.action.support.ActionFilters
import org.elasticsearch.action.support.PlainListenableActionFuture
import org.elasticsearch.action.support.TransportAction
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.groovy.AbstractESTestCase
import org.elasticsearch.tasks.TaskManager
import org.elasticsearch.threadpool.ThreadPool

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.rules.Timeout

import java.util.concurrent.CountDownLatch

/**
 * Tests {@link ListenableActionFutureExtensions}.
 */
class ListenableActionFutureExtensionsTests extends AbstractESTestCase {
    /**
     * Timeout for each <em>individual</em> test (and non-static to avoid wasted test resources).
     */
    @Rule
    public Timeout timeout = new Timeout(60000)
    /**
     * Used to collect each test name for creating the {@link #threadPool}.
     */
    @Rule
    public TestName testName = new TestName()

    /**
     * {@link ThreadPool} per test created to test the {@link ListenableActionFuture} behavior.
     */
    private ThreadPool threadPool

    @Before
    void createThreadPool() {
        threadPool = new ThreadPool(Settings.EMPTY)
    }

    @After
    void cleanUpThreadPool() {
        terminate(threadPool)
    }

    @Test
    void testListener_success() {
        NoOpResponse successResponse = null

        // wait until both callbacks have been invoked (also the reason for the timeout)
        CountDownLatch latch = new CountDownLatch(1)

        // arbitrary action performed to get a response
        ListenableActionFuture<NoOpResponse> future = requestSuccess()

        // generic listener receives both success and failure messages:
        assert ListenableActionFutureExtensions.listener(future) { response, e ->
            assert response != null
            assert e == null

            // remember the response
            successResponse = response

            // mark invocation #1
            latch.countDown()
        } == future

        NoOpResponse response = future.actionGet()

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
        ListenableActionFuture<NoOpResponse> future = requestFailure()

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
        NoOpResponse successResponse = null

        // wait until both callbacks have been invoked (also the reason for the timeout)
        CountDownLatch latch = new CountDownLatch(1)

        // arbitrary action performed to get a response
        ListenableActionFuture<NoOpResponse> future = requestSuccess()

        // generic listener receives both success and failure messages:
        assert ListenableActionFutureExtensions.successListener(future) {
            assert it != null

            // remember the response
            successResponse = it

            // mark invocation #1
            latch.countDown()
        } == future

        NoOpResponse response = future.actionGet()

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
        ListenableActionFuture<NoOpResponse> future = requestFailure()

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
        NoOpResponse responseFromListener = null
        NoOpResponse responseFromSuccess1 = null
        NoOpResponse responseFromSuccess2 = null

        // arbitrary action performed to get a response
        ListenableActionFuture<NoOpResponse> future = requestSuccess()

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
        NoOpResponse response = future.actionGet()

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

        ListenableActionFuture<NoOpResponse> successfulFuture = requestSuccess()
        // note: this breaks because the mapping is invalid after running successfulFuture
        ListenableActionFuture<NoOpResponse> failedFuture = requestFailure()

        // NOTE: Setting up to see success (with asserts) and failure (without)
        // Only checking the result for one of the listeners, then chaining the other

        // should return itself for chaining
        assert successfulFuture.listener { NoOpResponse response, Throwable e ->
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
            assert successfulFuture.actionGet() != null
            failedFuture.actionGet()

            fail("Expected failure.")
        }
        catch (ElasticsearchException expected) {
            // expected exception; now ensure that the callbacks are called
            latch.await()
        }
    }

    /**
     * Create a {@link ListenableActionFuture} always handles a {@link NoOpSuccessAction} (and therefore always
     * succeeds).
     */
    private ListenableActionFuture<NoOpResponse> requestSuccess() {
        makeRequest(new NoOpSuccessAction())
    }

    /**
     * Create a {@link ListenableActionFuture} always handles a {@link NoOpFailureAction} (and therefore always fails).
     */
    private ListenableActionFuture<NoOpResponse> requestFailure() {
        makeRequest(new NoOpFailureAction())
    }

    /**
     * Create a {@link ListenableActionFuture} that is used to handle the execution of the {@code action}.
     *
     * @param action The action to {@link TransportAction#execute} against the returned future
     * @return Never {@code null}.
     */
    private ListenableActionFuture<NoOpResponse> makeRequest(TransportAction<NoOpRequest, NoOpResponse> action) {
        ListenableActionFuture<NoOpResponse> future = new PlainListenableActionFuture<>(threadPool)

        // invoke the action that sends a success/failure "response" to the future
        action.execute(new NoOpRequest(), future)

        future
    }

    /**
     * A {@link TransportAction} without filters, settings, and a thread pool.
     */
    static abstract class AbstractNoOpAction extends TransportAction<NoOpRequest, NoOpResponse> {
        protected AbstractNoOpAction(String actionName) {
            super(Settings.EMPTY, actionName, null, new ActionFilters(Collections.emptySet()),
                  new IndexNameExpressionResolver(Settings.EMPTY), new TaskManager(Settings.EMPTY))
        }
    }

    /**
     * An {@link AbstractNoOpAction} that always succeeds.
     */
    static class NoOpSuccessAction extends AbstractNoOpAction {
        /**
         * Creates a {@code NoOpSuccessAction} named "Successful Action".
         */
        public NoOpSuccessAction() {
            super("Successful Action")
        }

        /**
         * Send the {@code listener} a new {@link NoOpResponse}.
         *
         * @param request Ignored.
         * @param listener Given a new {@link NoOpResponse}.
         */
        @Override
        protected void doExecute(NoOpRequest request, ActionListener<NoOpResponse> listener) {
            listener.onResponse(new NoOpResponse())
        }
    }

    /**
     * An {@link AbstractNoOpAction} that always fails.
     */
    static class NoOpFailureAction extends AbstractNoOpAction {
        /**
         * Creates a {@code NoOpFailureAction} named "Failure Action".
         */
        public NoOpFailureAction() {
            super("Failure Action")
        }

        /**
         * Send the {@code listener} a new {@link ElasticsearchException}.
         *
         * @param request Ignored.
         * @param listener Given a new {@link ElasticsearchException}.
         */
        @Override
        protected void doExecute(NoOpRequest request, ActionListener<NoOpResponse> listener) {
            listener.onFailure(new ElasticsearchException("NoOpFailureAction expected to fail"))
        }
    }

    /**
     * An {@link ActionRequest} that can be used to test asynchronous requests.
     */
    static class NoOpRequest extends ActionRequest {
        /**
         * No validation takes place.
         *
         * @return Always {@code null}.
         */
        @Override
        public ActionRequestValidationException validate() {
            return null;
        }
    }

    /**
     * An {@link ActionResponse} that can be used to test asynchronous responses.
     */
    static class NoOpResponse extends ActionResponse {
        // intentionally empty
    }
}
