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
package org.elasticsearch.groovy.action.mlt

import org.elasticsearch.action.mlt.MoreLikeThisRequest
import org.elasticsearch.action.mlt.MoreLikeThisRequestBuilder
import org.elasticsearch.client.Client

/**
 * {@code MoreLikeThisRequestExtensions} provides Groovy-friendly {@link MoreLikeThisRequest} extensions.
 * @see Client#moreLikeThis(MoreLikeThisRequest)
 */
class MoreLikeThisRequestExtensions {
    /**
     * Sets the optional search {@code source} that can reduce the number of documents checked to be more-like-this.
     *
     * @param self The {@code this} reference for the {@link MoreLikeThisRequest}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static MoreLikeThisRequest searchSource(MoreLikeThisRequest self, Closure source) {
        self.searchSource(source.asJsonBytes())
    }

    /**
     * Sets the optional search {@code source} that can reduce the number of documents checked to be more-like-this.
     *
     * @param self The {@code this} reference for the {@link MoreLikeThisRequestBuilder}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static MoreLikeThisRequestBuilder setSearchSource(MoreLikeThisRequestBuilder self, Closure source) {
        self.setSearchSource(source.asJsonBytes())
    }
}