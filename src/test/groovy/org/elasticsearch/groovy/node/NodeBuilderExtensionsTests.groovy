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

import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.Node
import org.elasticsearch.node.NodeBuilder
import org.elasticsearch.test.ElasticsearchIntegrationTest

import org.junit.Test

import static org.elasticsearch.node.NodeBuilder.nodeBuilder

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verifyZeroInteractions
import static org.mockito.Mockito.when

/**
 * Tests {@link NodeBuilderExtensions}.
 */
class NodeBuilderExtensionsTests extends ElasticsearchIntegrationTest {
    /**
     * Tested {@link NodeBuilder}.
     */
    private final NodeBuilder builder = nodeBuilder()

    @Test
    void testSettingsClosure() {
        String clusterName = randomAsciiOfLength(scaledRandomIntBetween(1, 128))
        boolean clientNode = randomBoolean()
        boolean dataNode = randomBoolean()
        boolean localNode = randomBoolean()
        int arbitraryField = randomInt()

        builder.settings {
            arbitrary {
                field = arbitraryField
            }
            cluster {
                name = clusterName
            }
            node {
                client = clientNode
                data = dataNode
                local = localNode
            }
        }

        // only testing the settings not the node
        Settings settings = builder.settings.build()

        assert settings.getAsBoolean("node.client", null) == clientNode
        assert settings.getAsBoolean("node.data", null) == dataNode
        assert settings.getAsBoolean("node.local", null) == localNode
        assert settings.get("cluster.name") == clusterName
        assert settings.getAsInt("arbitrary.field", null) == arbitraryField
    }

    @Test
    void testSettings() {
        String clusterName = randomAsciiOfLength(scaledRandomIntBetween(1, 128))
        boolean clientNode = randomBoolean()
        boolean dataNode = randomBoolean()
        boolean localNode = randomBoolean()

        builder.clusterName(clusterName).client(clientNode).data(dataNode).local(localNode)

        Settings settings = builder.settings.build()

        assert settings.getAsBoolean("node.client", null) == clientNode
        assert settings.getAsBoolean("node.data", null) == dataNode
        assert settings.getAsBoolean("node.local", null) == localNode
        assert settings.get("cluster.name") == clusterName
    }

    @Test
    void testNodeBuilder() {
        builder.settings {
            node {
                local = true
            }
            cluster {
                name = 'test'
            }
        }

        // ensure that it always returns a Node
        Node closeNode = builder.node()

        // make sure that we close it and that it returns a reference to itself (as opposed to null)
        closeNode.stop().close()
    }

    @Test
    void testGetSettings() {
        NodeBuilder builder = mock(NodeBuilder)

        ImmutableSettings.Builder settings = mock(ImmutableSettings.Builder)

        when(builder.settings()).thenReturn(settings)

        assert NodeBuilderExtensions.getSettings(builder) == settings

        verifyZeroInteractions(settings)
    }

    @Test
    void testExtensionModuleConfigured() {
        assert builder.getSettings() == builder.settings()
    }
}