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
import org.elasticsearch.test.ElasticsearchIntegrationTest

import org.junit.Test

import static org.elasticsearch.node.NodeBuilder.nodeBuilder

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verifyZeroInteractions
import static org.mockito.Mockito.when

/**
 * Tests {@link NodeExtensions}.
 */
class NodeExtensionsTests extends ElasticsearchIntegrationTest {
    /**
     * Mock {@link Node} to ensure functionality.
     */
    private final Node node = mock(Node)

    @Test
    void testGetSettings() {
        Settings settings = mock(Settings)

        when(node.settings()).thenReturn(settings)

        assert NodeExtensions.getSettings(node) == settings

        verifyZeroInteractions(settings)
    }

    @Test
    void testGetClient() {
        Client client = mock(Client)

        when(node.client()).thenReturn(client)

        assert NodeExtensions.getClient(node) == client

        verifyZeroInteractions(client)
    }

    @Test
    void testExtensionModuleConfigured() {
        Node node = nodeBuilder().local(true).build()

        assert node.getClient() == node.client()
        assert node.getSettings() == node.settings()

        node.close()
    }
}