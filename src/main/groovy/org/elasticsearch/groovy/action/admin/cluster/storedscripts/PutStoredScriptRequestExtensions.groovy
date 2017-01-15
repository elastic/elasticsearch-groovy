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
package org.elasticsearch.groovy.action.admin.cluster.storedscripts

import org.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptRequest
import org.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptRequestBuilder
import org.elasticsearch.client.ClusterAdminClient
import org.elasticsearch.common.xcontent.XContentType

/**
 * {@code PutStoredScriptRequestExtensions} provides Groovy-friendly {@link PutStoredScriptRequest} extensions.
 * @see ClusterAdminClient#putStoredScript(PutStoredScriptRequest)
 */
class PutStoredScriptRequestExtensions {
    /**
     * Sets the content {@code source} (script) to index. Note: The script is a string that needs to be executed on the server, not
     * on your client!
     * <pre>
     * putStoredScriptRequest.script {
     *   script = "Math.max(doc['field'].value)"
     * }
     * </pre>
     *
     * @param self The {@code this} reference for the {@link PutStoredScriptRequest}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static PutStoredScriptRequest script(PutStoredScriptRequest self, Closure source) {
        self.script(source.build(XContentType.JSON).bytes())
    }

    /**
     * Sets the content {@code source} (script) to index.
     *
     * @param self The {@code this} reference for the {@link PutStoredScriptRequestBuilder}.
     * @param source The content source
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     */
    static PutStoredScriptRequestBuilder setSource(PutStoredScriptRequestBuilder self, Closure source) {
        self.setSource(source.build(XContentType.JSON).bytes())
    }
}