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
package org.elasticsearch.groovy.node

import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.Node

/**
 * {@code NodeExtensions} provides convenience methods to standard Elasticsearch {@link Node}s to make them more Groovy
 * friendly.
 */
class NodeExtensions {
    /**
     * Get the {@link Settings} used to create the {@link Node}.
     *
     * @param self The {@link Node} to invoke
     * @return Always {@link Node#settings()}.
     * @throws NullPointerException if {@code self} is {@code null}
     */
    static Settings getSettings(Node self) {
        self.settings()
    }

    /**
     * Get a {@link Client} that can be used to execute actions (operations) against the cluster.
     *
     * @param self The {@link Node} to invoke
     * @return Always {@link Node#settings()}.
     * @throws NullPointerException if {@code self} is {@code null}
     */
    static Client getClient(Node self) {
        self.client()
    }
}