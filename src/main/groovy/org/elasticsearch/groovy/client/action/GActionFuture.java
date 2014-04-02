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

package org.elasticsearch.groovy.client.action;

import groovy.lang.Closure;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.support.PlainListenableActionFuture;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.threadpool.ThreadPool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 */
public class GActionFuture<T> implements ListenableActionFuture<T>, ActionListener<T> {

    private final PlainListenableActionFuture<T> future;

    public GActionFuture(ListenableActionFuture<T> future) {
        this.future = (PlainListenableActionFuture<T>) future;
    }

    public GActionFuture(ThreadPool threadPool, ActionRequest request) {
        this.future = new PlainListenableActionFuture<T>(request.listenerThreaded(), threadPool);
    }

    public void setListener(final Closure listener) {
        addListener(new ActionListener<T>() {
            @Override
            public void onResponse(T t) {
                listener.call(this);
            }

            @Override
            public void onFailure(Throwable e) {
                listener.call(this);
            }
        });
    }

    public void setSuccess(final Closure success) {
        addListener(new ActionListener<T>() {
            @Override
            public void onResponse(T t) {
                success.call(t);
            }

            @Override
            public void onFailure(Throwable e) {
                // ignore
            }
        });
    }

    public void setFailure(final Closure failure) {
        addListener(new ActionListener<T>() {
            @Override
            public void onResponse(T t) {
                // nothing
            }

            @Override
            public void onFailure(Throwable e) {
                failure.call(e);
            }
        });
    }

    public T getResponse() {
        return actionGet();
    }

    public T response(String timeout) throws ElasticsearchException {
        return actionGet(timeout);
    }

    public T response(long timeoutMillis) throws ElasticsearchException {
        return actionGet(timeoutMillis);
    }

    public T response(TimeValue timeout) throws ElasticsearchException {
        return actionGet(timeout);
    }

    public T response(long timeout, TimeUnit unit) throws ElasticsearchException {
        return actionGet(timeout, unit);
    }

    @Override
    public void onResponse(T t) {
        future.onResponse(t);
    }

    @Override
    public void onFailure(Throwable e) {
        future.onFailure(e);
    }

    // delegate methods

    public void addListener(ActionListener<T> tActionListener) {
        future.addListener(tActionListener);
    }

    @Override
    public void addListener(Runnable listener) {
        future.addListener(listener);
    }

    @Override
    public T actionGet() throws ElasticsearchException {
        return future.actionGet();
    }

    @Override
    public T actionGet(String timeout) throws ElasticsearchException {
        return future.actionGet(timeout);
    }

    @Override
    public T actionGet(long timeoutMillis) throws ElasticsearchException {
        return future.actionGet(timeoutMillis);
    }

    @Override
    public T actionGet(long timeout, TimeUnit unit) throws ElasticsearchException {
        return future.actionGet(timeout, unit);
    }

    @Override
    public T actionGet(TimeValue timeout) throws ElasticsearchException {
        return future.actionGet(timeout);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    @Override
    public Throwable getRootFailure() {
        return future.getRootFailure();
    }
}
