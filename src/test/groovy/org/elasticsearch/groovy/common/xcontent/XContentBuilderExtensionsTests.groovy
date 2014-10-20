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
package org.elasticsearch.groovy.common.xcontent

import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.common.xcontent.XContentParser
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.test.ElasticsearchTestCase

import org.junit.Test

/**
 * Tests {@link XContentBuilderExtensions}.
 */
class XContentBuilderExtensionsTests extends ElasticsearchTestCase {
    /**
     * Get a random {@link XContentType} to ensure all types work.
     */
    private final XContentType type = XContentType.values()[randomInt(XContentType.values().length - 1)]

    /**
     * Reproducible number.
     */
    private final long value = randomLong()
    /**
     * Reproducible string.
     */
    private final String nestedName = randomAsciiOfLengthBetween(1, 8)

    /**
     * {@link Closure} used to test mappings.
     */
    private final Closure closure = {
        id = value
        nested {
            name = nestedName
        }
    }

    /**
     * {@link XContentBuilder} to ensure functionality.
     */
    private final XContentBuilder jsonBuilder = XContentFactory.contentBuilder(XContentType.JSON)

    @Test
    void testAsJsonBytes() {
        byte[] bytes = XContentBuilderExtensions.asJsonBytes(closure)
        XContentParser jsonParser = XContentType.JSON.xContent().createParser(bytes)

        assert closure.asMap() == jsonParser.mapAndClose()
    }

    @Test
    void testAsJsonString() {
        String string = XContentBuilderExtensions.asJsonString(closure)
        XContentParser jsonParser = XContentType.JSON.xContent().createParser(string)

        assert closure.asMap() == jsonParser.mapAndClose()
    }

    @Test
    void testBuild() {
        XContentBuilder builder = XContentBuilderExtensions.build(closure, type)

        assert type == builder.contentType()
        assert closure.asMap() == type.xContent().createParser(builder.bytes()).mapAndClose()
    }

    @Test
    void testBuildBytes() {
        byte[] bytes = XContentBuilderExtensions.buildBytes(closure, XContentType.SMILE)

        assert closure.asMap() == XContentType.SMILE.xContent().createParser(bytes).mapAndClose()
    }

    @Test
    void testBuildString() {
        // avoid using SMILE because it will [expectedly] fail with strings (so YAML was picked to avoid using hitting
        //  SMILE and reusing JSON)
        String string = XContentBuilderExtensions.buildString(closure, XContentType.YAML)

        assert closure.asMap() == XContentType.YAML.xContent().createParser(string).mapAndClose()
    }

    @Test
    void testGetBytes() {
        assert XContentBuilderExtensions.getBytes(jsonBuilder) == jsonBuilder.bytes()
    }

    @Test
    void testGetByteStream() {
        assert XContentBuilderExtensions.getBytesStream(jsonBuilder) == jsonBuilder.bytesStream()
    }

    @Test
    void testGetGenerator() {
        assert XContentBuilderExtensions.getGenerator(jsonBuilder) == jsonBuilder.generator()
    }

    @Test
    void testGetStream() {
        assert XContentBuilderExtensions.getStream(jsonBuilder) == jsonBuilder.stream()
    }

    @Test
    void testGetString() {
        assert XContentBuilderExtensions.getString(jsonBuilder) == jsonBuilder.string()
    }

    @Test
    void testExtensionModuleConfigured() {
        // map the same closure
        XContentBuilder arbitraryBuilder = XContentFactory.contentBuilder(type).map(closure)
        XContentBuilder jsonBuilder = XContentFactory.contentBuilder(XContentType.JSON).map(closure)

        assert closure.asJsonString() == jsonBuilder.string()
        assert closure.asJsonBytes() == jsonBuilder.bytes().array()
        assert closure.buildString(type) == arbitraryBuilder.string()
        assert closure.build(type).string() == arbitraryBuilder.string()
        assert closure.buildBytes(type) == arbitraryBuilder.bytes().array()
        assert jsonBuilder.bytes() == jsonBuilder.getBytes()
        assert jsonBuilder.bytesStream() == jsonBuilder.getBytesStream()
        assert jsonBuilder.generator() == jsonBuilder.getGenerator()
        assert jsonBuilder.stream() == jsonBuilder.getStream()
        assert jsonBuilder.string() == jsonBuilder.getString()
    }
}