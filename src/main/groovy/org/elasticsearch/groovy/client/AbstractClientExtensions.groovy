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

import org.elasticsearch.action.ActionRequest
import org.elasticsearch.action.ActionResponse
import org.elasticsearch.action.support.PlainListenableActionFuture
import org.elasticsearch.client.ElasticsearchClient

/**
 * {@code AbstractClientExtensions} provides convenience operations for {@link org.elasticsearch.client.Client}
 * extensions.
 */
abstract class AbstractClientExtensions {
    /**
     * Wraps the {@code request} and creates a {@link PlainListenableActionFuture} associated with the {@link
     * ActionResponse} in the same wrapper for simple reuse.
     *
     * @param client The client to perform the {@code request}
     * @param request The request to make
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    protected static
        <Request extends ActionRequest,
         Response extends ActionResponse,
         Client extends ElasticsearchClient<Client>> Wrapper<Request, Response, Client> wrap(Client client,
                                                                                             Request request) {
        new Wrapper<>(client, request);
    }

    /**
     * Configures the {@code request} and creates a {@link PlainListenableActionFuture} associated with the {@link
     * ActionResponse} in the same wrapper for simple reuse.
     *
     * @param client The client to perform the {@code request}
     * @param request The request to make
     * @param requestClosure The configuration of the {@code request}
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    protected static
        <Request extends ActionRequest,
         Response extends ActionResponse,
         Client extends ElasticsearchClient<Client>> Wrapper<Request, Response, Client> wrap(Client client,
                                                                                             Request request,
                                                                                             Closure requestClosure) {
        // configure the request
        request.with(requestClosure)

        wrap(client, request);
    }

    /**
     * Wrapper for a {@link ActionRequest} and {@link ActionResponse}.
     * <p />
     * This is used to reduce the copy/paste across the various methods of this extension class.
     *
     * @param <Request> The {@link ActionRequest} to contain
     * @param <Response> The {@link ActionResponse} to respond with
     * @param <Client> The {@link ElasticsearchClient} to used to execute the request
     */
    static class Wrapper<Request extends ActionRequest,
                         Response extends ActionResponse,
                         Client extends ElasticsearchClient<Client>> {
        /**
         * The request to process.
         */
        final Request request;
        /**
         * The {@link #request}'s associated response.
         */
        final PlainListenableActionFuture<Response> responseFuture;

        /**
         * Create a new {@link Wrapper} that uses the {@code client} and {@code request} to hold a {@link
         * PlainListenableActionFuture} for the response.
         *
         * @param client The client to perform the {@code request}
         * @param request The request to make
         * @throws NullPointerException if any parameter is {@code null}
         */
        Wrapper(Client client, Request request) {
            // required
            this.responseFuture = new PlainListenableActionFuture<>(request.listenerThreaded(), client.threadPool());
            this.request = request;
        }
    }
}