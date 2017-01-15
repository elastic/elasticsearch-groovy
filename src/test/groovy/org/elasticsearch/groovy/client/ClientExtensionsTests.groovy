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

import org.elasticsearch.client.AdminClient
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.groovy.AbstractESTestCase
import org.elasticsearch.node.Node

import org.junit.Test

import static org.elasticsearch.node.NodeBuilder.nodeBuilder

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verifyZeroInteractions
import static org.mockito.Mockito.when


/**
 * Tests {@link ClientExtensions}.
 */
class ClientExtensionsTests extends AbstractESTestCase {
    /**
     * Mock {@link Client} to ensure functionality.
     */
    private final Client client = mock(Client)

    @Test
    void testGetAdmin() {
        AdminClient admin = mock(AdminClient)

        when(client.admin()).thenReturn(admin)

        assert ClientExtensions.getAdmin(client) == admin

        verifyZeroInteractions(admin)
    }

    @Test
    void testGetIndices() {
        Settings settings = Settings.EMPTY

        when(client.settings()).thenReturn(settings)

        assert ClientExtensions.getSettings(client) == settings
    }

    @Test
    void testExtensionModuleConfigured() {
        Node node = nodeBuilder().local(true).settings { path.home = createTempDir().toString() }.build()
        Client client = node.client()

        assert client.getAdmin() == client.admin()
        assert client.getSettings() == client.settings()

        node.close()
    }
}