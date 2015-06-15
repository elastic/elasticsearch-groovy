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
package org.elasticsearch.groovy.common.settings

import org.elasticsearch.common.settings.Settings
import org.elasticsearch.groovy.AbstractElasticsearchTestCase

import org.junit.Test

/**
 * Tests {@link SettingsBuilderExtensions}.
 */
class SettingsBuilderExtensionsTests extends AbstractElasticsearchTestCase {
    /**
     * Tested {@link ImmutableSettings.Builder}.
     */
    private final ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder()

    @Test
    void testSettingsClosure() {
        String clusterName = randomAsciiOfLengthBetween(1, 128)
        boolean clientNode = randomBoolean()
        boolean dataNode = randomBoolean()
        boolean localNode = randomBoolean()
        int arbitraryField = randomInt()

        SettingsBuilderExtensions.put(builder) {
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

        // verify that the settings were added appropriately
        Settings settings = builder.build()

        assert settings.getAsBoolean("node.client", null) == clientNode
        assert settings.getAsBoolean("node.data", null) == dataNode
        assert settings.getAsBoolean("node.local", null) == localNode
        assert settings.get("cluster.name") == clusterName
        assert settings.getAsInt("arbitrary.field", null) == arbitraryField
    }

    @Test
    void testExtensionModuleConfigured() {
        ImmutableSettings.Builder mapBuilder = ImmutableSettings.settingsBuilder()

        String value = randomAsciiOfLengthBetween(16, 128)

        // same key/value
        Map<String, String> settingsMap = [key : value]
        Closure settingsClosure = { key = value }

        // behave the same
        assert builder.put(settingsClosure) == builder
        assert mapBuilder.put(settingsMap) == mapBuilder

        // built from the closure
        Settings settings = builder.build()

        // actually defined and not null, matching the same value in the map version
        assert settings.get('key') != null
        assert settings.get('key') == mapBuilder.build().get('key')
    }
}