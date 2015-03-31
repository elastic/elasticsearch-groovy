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
     * Uses the {@code request} and creates an {@link ActionResponse} using the {@code requestClosure} to generate it.
     *
     * @param client The client to perform the {@code request}
     * @param request The request to make
     * @param requestClosure The method/closure to invoke given the sole argument of {@code request}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}.
     */
    protected static
        <Request extends ActionRequest,
         Response extends ActionResponse,
         Client extends ElasticsearchClient<Client>> Response doRequest(Client client,
                                                                        Request request,
                                                                        Closure<Response> requestClosure) {
        // TODO: After https://github.com/elastic/elasticsearch/issues/9201 is merged, then we can change this to avoid
        //       unnecessary threading (and simplify this method by potentially dropping parameters)!
        doRequestAsync(client, request, requestClosure).actionGet()
    }

    /**
     * Configures the {@code request} and creates an {@link ActionResponse} using the {@code requestClosure} to
     * generate it.
     * <p>
     * The {@code request} is configured using the {@code requestConfig} if it is non-{@code null}.
     *
     * @param client The client to perform the {@code request}
     * @param request The request to make
     * @param requestConfig The configuration of the {@code request}
     * @param requestClosure The method/closure to invoke given the sole argument of {@code request}.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter except {@code requestConfig} is {@code null}.
     */
    protected static
        <Request extends ActionRequest,
         Response extends ActionResponse,
         Client extends ElasticsearchClient<Client>> Response doRequest(Client client,
                                                                        Request request,
                                                                        Closure requestConfig,
                                                                        Closure<Response> requestClosure) {
        // configure the request
        if (requestConfig != null) {
            request.with(requestConfig)
        }

        doRequest(client, request, requestClosure)
    }

    /**
     * Uses the {@code request} and creates a {@link PlainListenableActionFuture} associated with the {@link
     * ActionResponse} using the {@code requestClosure} to generate it.
     *
     * @param client The client to perform the {@code request}
     * @param request The request to make
     * @param requestClosure The method/closure to invoke given the first argument of {@code request} and a
     *                       {@link PlainListenableActionFuture} as the second argument.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter is {@code null}.
     */
    protected static
        <Request extends ActionRequest,
         Response extends ActionResponse,
         Client extends ElasticsearchClient<Client>> PlainListenableActionFuture<Response> doRequestAsync(Client client,
                                                                                                          Request request,
                                                                                                          Closure<Response> requestClosure) {
        PlainListenableActionFuture<Response> responseFuture =
                new PlainListenableActionFuture<>(request.listenerThreaded(), client.threadPool())

        // invoke the request closure (method) that takes the request/response future to respond to
        requestClosure.call(request, responseFuture)

        responseFuture
    }

    /**
     * Configures the {@code request} and creates a {@link PlainListenableActionFuture} associated with the {@link
     * ActionResponse} using the {@code requestClosure} to generate it.
     * <p>
     * The {@code request} is configured using the {@code requestConfig} if it is non-{@code null}.
     *
     * @param client The client to perform the {@code request}
     * @param request The request to make
     * @param requestConfig The configuration of the {@code request}
     * @param requestClosure The method/closure to invoke given the first argument of {@code request} and a
     *                       {@link PlainListenableActionFuture} as the second argument.
     * @return Never {@code null}.
     * @throws NullPointerException if any parameter except {@code requestConfig} is {@code null}.
     */
    protected static
        <Request extends ActionRequest,
         Response extends ActionResponse,
         Client extends ElasticsearchClient<Client>> PlainListenableActionFuture<Response> doRequestAsync(Client client,
                                                                                                          Request request,
                                                                                                          Closure requestConfig,
                                                                                                          Closure<Response> requestClosure) {
        // configure the request
        if (requestConfig != null) {
            request.with(requestConfig)
        }

        doRequestAsync(client, request, requestClosure)
    }
}