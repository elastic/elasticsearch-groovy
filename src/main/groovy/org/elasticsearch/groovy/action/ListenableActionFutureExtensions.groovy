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

import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.ListenableActionFuture

import static org.elasticsearch.common.Preconditions.checkNotNull

/**
 * {@code ListenableActionFutureExtensions} adds Groovy-friendly extensions to {@link ListenableActionFuture}s to
 * enable sometimes-simpler listener support.
 * <p />
 * Note: All of the listener methods behave like "add" methods. "add" was not used to avoid colliding with Java
 * variants to avoid unexpected issues ({@link Closure} is-a {@link Runnable}, which can cause confusion without explicit casting).
 * <pre>
 * listenableActionFuture.listener { response, e ->
 *     // ...
 * }.successListener {
 *     // ...
 * }.failureListener {
 *     // ...
 * }
 * </pre>
 */
class ListenableActionFutureExtensions {
    /**
     * Adds the {@code listener} as a wrapped {@link ActionListener}. In both response (success or failure), the first
     * parameter passed to the {@code listener} is the {@link ListenableActionFuture} itself ({@code self}), the second
     * parameter is the successful response ({@code null} upon failure), and the third parameter is the
     * {@link Throwable} exception ({@code null} success).
     * <pre>
     * listenableActionFuture.listener { T response, Throwable t ->
     *     if (t != null) {
     *         // error
     *     }
     *     else {
     *         // success (NOTE: response could still be null if that was the value returned)
     *     }
     * }
     * </pre>
     * The other listener extensions provide specific handling for either
     *
     * @param self The {@code this} reference for the {@link ListenableActionFuture}.
     * @param listener The generic listener
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static <T> ListenableActionFuture<T> listener(ListenableActionFuture<T> self, Closure listener) {
        checkNotNull(listener, (Object)"listener cannot be null")

        self.addListener(new ActionListener() {
            @Override
            void onResponse(Object t) {
                listener.call(t, null)
            }

            @Override
            void onFailure(Throwable e) {
                listener.call(null, e)
            }
        })

        self
    }

    /**
     * Adds the {@code listener} as a wrapped {@link ActionListener} that only handles
     * {@link ActionListener#onResponse success}.
     * <pre>
     * listenableActionFuture.successListener { response ->
     *     // response can be null if it was a valid return value (same as actionGet())
     * }
     * </pre>
     * It is very important to note that successful responses are inherently not guaranteed to triggered because not
     * every {@link ListenableActionFuture} will succeed. It is therefore a good idea to use this in conjunction with
     * {@link #failureListener(ListenableActionFuture, Closure)}.
     *
     * @param self The {@code this} reference for the {@link ListenableActionFuture}.
     * @param listener The success listener
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static <T> ListenableActionFuture<T> successListener(ListenableActionFuture<T> self, Closure listener) {
        checkNotNull(listener, (Object)"listener cannot be null")

        self.addListener(new ActionListener() {
            @Override
            void onResponse(Object t) {
                listener.call(t)
            }

            @Override
            void onFailure(Throwable e) {
                // success listener ignores failure
            }
        })

        self
    }

    /**
     * Adds the {@code listener} as a wrapped {@link ActionListener} that only handles
     * {@link ActionListener#onFailure(Throwable)} failure}.
     * <pre>
     * listenableActionFuture.failureListener { e ->
     *     // e is never null
     * }
     * </pre>
     * It is very important to note that failure responses are inherently not guaranteed to be triggered because not
     * every {@link ListenableActionFuture} will fail. It is therefore a good idea to use this in conjunction with
     * {@link #successListener(ListenableActionFuture, Closure)}.
     *
     * @param self The {@code this} reference for the {@link ListenableActionFuture}.
     * @param listener The failure listener
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static <T> ListenableActionFuture<T> failureListener(ListenableActionFuture<T> self, Closure listener) {
        checkNotNull(listener, (Object)"listener cannot be null")

        self.addListener(new ActionListener() {
            @Override
            void onResponse(Object t) {
                // failure listener ignores success
            }

            @Override
            void onFailure(Throwable e) {
                listener.call(e)
            }
        })

        self
    }
}
