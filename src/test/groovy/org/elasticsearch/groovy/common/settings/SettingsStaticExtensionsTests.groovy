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

import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.groovy.AbstractElasticsearchTestCase

import org.junit.Test

/**
 * Tests {@link SettingsStaticExtensions}.
 */
class SettingsStaticExtensionsTests extends AbstractElasticsearchTestCase {

    @Test
    void testSettingsClosure() {
        String clusterName = randomAsciiOfLengthBetween(1, 128)
        boolean clientNode = randomBoolean()
        boolean dataNode = randomBoolean()
        boolean localNode = randomBoolean()
        int arbitraryField = randomInt()

        // verify that the settings were added appropriately (note: first arg is unused and not shown in the actual
        //  extension version, as shown in the final test in this test class)
        Settings settings = SettingsStaticExtensions.settingsBuilder(null) {
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
        }.build()

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

        // same key/value as used in the closure
        mapBuilder.put([key : value])

        // built from the closure
        Settings closureSettings = ImmutableSettings.settingsBuilder {
            key = value
        }.build()

        // actually defined and not null, matching the same value in the map version
        assert closureSettings.get('key') != null
        assert closureSettings.get('key') == mapBuilder.build().get('key')
    }
}