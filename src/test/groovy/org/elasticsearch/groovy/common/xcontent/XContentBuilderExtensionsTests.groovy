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
import org.elasticsearch.groovy.AbstractESTestCase
import org.junit.After
import org.junit.Test

/**
 * Tests {@link XContentBuilderExtensions}.
 */
class XContentBuilderExtensionsTests extends AbstractESTestCase {
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

    /**
     * If non-{@code null}, this will be automatically closed after testing.
     */
    private XContentParser parser = null

    /**
     *
     */
    @After
    void closeParser() {
        parser?.close()
    }

    @Test
    void testAsJsonBytes() {
        byte[] bytes = XContentBuilderExtensions.asJsonBytes(closure)

        // close the parser after it's used
        parser = XContentType.JSON.xContent().createParser(bytes)

        assert closure.asMap() == parser.map()
    }

    @Test
    void testAsJsonString() {
        String string = XContentBuilderExtensions.asJsonString(closure)

        // close the parser after it's used
        parser = XContentType.JSON.xContent().createParser(string)

        assert closure.asMap() == parser.map()
    }

    @Test
    void testBuild() {
        XContentBuilder builder = XContentBuilderExtensions.build(closure, type)

        // close the parser after it's used
        parser = type.xContent().createParser(builder.bytes())

        assert type == builder.contentType()
        assert closure.asMap() == parser.map()
    }

    @Test
    void testBuildBytes() {
        byte[] bytes = XContentBuilderExtensions.buildBytes(closure, type)

        // close the parser after it's used
        parser = type.xContent().createParser(bytes)

        assert closure.asMap() == parser.map()
    }

    @Test
    void testBuildString() {
        // avoid using CBOR/SMILE because it will [expectedly] fail with strings (so YAML was picked explicitly to
        //  avoid using the binary only formats and unnecessarily reusing JSON)
        String string = XContentBuilderExtensions.buildString(closure, XContentType.YAML)

        // close the parser after it's used
        parser = XContentType.YAML.xContent().createParser(string)

        assert closure.asMap() == parser.map()
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
        assert closure.asJsonBytes() == jsonBuilder.bytes().toBytes()
        assert closure.buildString(type) == arbitraryBuilder.string()
        assert closure.build(type).string() == arbitraryBuilder.string()
        assert closure.buildBytes(type) == arbitraryBuilder.bytes().toBytes()
        assert jsonBuilder.bytes() == jsonBuilder.getBytes()
        assert jsonBuilder.bytesStream() == jsonBuilder.getBytesStream()
        assert jsonBuilder.generator() == jsonBuilder.getGenerator()
        assert jsonBuilder.stream() == jsonBuilder.getStream()
        assert jsonBuilder.string() == jsonBuilder.getString()
    }

    // simple tests to ensure accuracy (these are also covered in the ClosureExtension tests, though less specifically)
    // all of these require that the extension module be configured

    @Test
    void testSingleProperty() {
        assert '{"rootprop":"something"}' == { rootprop = 'something' }.asJsonString()
    }

    @Test
    void testArrayAndProperty() {
        assert '{"categories":["a","b","c"],"rootprop":"something"}' == {
            categories = ['a', 'b', 'c']
            rootprop = 'something'
        }.asJsonString()
    }

    @Test
    void testDotObjects() {
        assert '{"categories":["a","b","c"],"rootprop":"something","test.subprop":10}' == {
            categories = ['a', 'b', 'c']
            rootprop = 'something'
            test.subprop = 10
        }.asJsonString()
    }

    @Test
    void testNestedObjects() {
        assert '{"categories":["a","b","c"],"rootprop":"something","test":{"subprop":10}}' == {
            categories = ['a', 'b', 'c']
            rootprop = 'something'
            test {
                subprop = 10
            }
        }.asJsonString()
    }

    @Test
    void testAssignedNestedObjects() {
        assert '{"categories":["a","b","c"],"rootprop":"something","test":{"subprop":10}}' == {
            categories = ['a', 'b', 'c']
            rootprop = 'something'
            test = {
                subprop = 10
            }
        }.asJsonString()
    }

    @Test
    void testMapObjects() {
        assert '{"categories":["a","b","c"],"rootprop":"something","test":{"subprop":10,"three":[1,2,3]}}' == {
            categories = ['a', 'b', 'c']
            rootprop = 'something'
            test subprop: 10, three: [1, 2, 3]
        }.asJsonString()
    }

    @Test
    void testArrayOfClosures() {
        assert '{"foo":[{"bar":"hello"},{"hello":"bar"}]}' == {
            foo = [{ bar = 'hello'}, { hello = 'bar' }]
        }.asJsonString()
    }

    @Test
    void testExampleFromReferenceGuide() {
        List<String> results = ['one', 'two', 'three']

        assert '{"books":[{"title":"one"},{"title":"two"},{"title":"three"}]}' == {
            books = results.collect {
                [title: it]
            }
        }.asJsonString()
    }

    @Test
    void testAppendToList() {
        List<String> results = ['one', 'two', 'three']

        assert '{"books":[{"title":"one"},{"title":"two"},{"title":"three"}]}' == {
            books = []
            for (b in results) {
                books << [title: b]
            }
        }.asJsonString()
    }

    @Test
    void testReusedClosure() {
        List<String> results = ['one', 'two', 'three']

        Closure closure = {
            rootprop = 'something'
            books = results.collect {
                [title: it]
            }
        }

        assert closure.asJsonString() == closure.asJsonString()
        assert '{"rootprop":"something","books":[{"title":"one"},{"title":"two"},{"title":"three"}]}' == closure.asJsonString()
    }
}