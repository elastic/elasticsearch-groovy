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

import org.elasticsearch.common.bytes.BytesReference
import org.elasticsearch.common.io.BytesStream
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.XContentBuilderString
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.common.xcontent.XContentGenerator
import org.elasticsearch.common.xcontent.XContentType

/**
 * {@link XContentBuilderExtensions} adds Groovy-friendly extensions to {@link XContentBuilder} as well as directly to
 * {@link Closure}s.
 * <p />
 * In particular, this adds the ability to convert a {@link Closure} into a {@link Map} to a {@link XContentBuilder} to
 * enable effortless conversion:
 * <pre>
 * String json = XContentFactory.contentBuilder(XContentType.JSON).map {
 *   name = {
 *     first = "FirstName"
 *     last = "LastName"
 *   }
 *   // Not necessarily the recommended way to make a Date object:
 *   dob = new SimpleDateFormat("yyyy-MMM-dd").parse("2014-NOV-01")
 *   address = {
 *     street1 = "Street"
 *     city = "City"
 *     state = "State"
 *     country = "Country"
 *     postalCode = "12345"
 *   }
 * }.string
 * </pre>
 * Using the extensions provided here, this can be reduced even further to:
 * <pre>
 * String json = {
 *   name = {
 *     first = "FirstName"
 *     last = "LastName"
 *   }
 *   // Not necessarily the recommended way to make a Date object:
 *   dob = new SimpleDateFormat("yyyy-MMM-dd").parse("2014-NOV-01")
 *   address = {
 *     street1 = "Street"
 *     city = "City"
 *     state = "State"
 *     country = "Country"
 *     postalCode = "12345"
 *   }
 * }.asJsonString()
 * </pre>
 */
class XContentBuilderExtensions {
    /**
     * Convert the {@link Closure} into a {@code Map} and return the {@link org.elasticsearch.common.xcontent.XContentType#JSON JSON} {@code byte}
     * equivalent.
     *
     * @param self The {@code this} reference for the {@link Closure}
     * @param type The type of {@code XContent} to create in byte form
     * @return Never {@code null}.
     * @throws NullPointerException if {@code self} is {@code null}
     * @throws org.elasticsearch.ElasticsearchIllegalArgumentException if {@code type} is unrecognized
     * @throws IOException if any error occurs while mapping the {@link Closure}
     */
    static byte[] asJsonBytes(Closure self) throws IOException {
        buildBytes(self, XContentType.JSON)
    }

    /**
     * Convert the {@link Closure} into a {@code Map} and return the {@link XContentType#JSON JSON} string equivalent.
     *
     * @param self The {@code this} reference for the {@link Closure}
     * @param type The type of {@code XContent} to create in byte form
     * @return Never blank.
     * @throws NullPointerException if {@code self} is {@code null}
     * @throws org.elasticsearch.ElasticsearchIllegalArgumentException if {@code type} is unrecognized
     * @throws IOException if any error occurs while mapping the {@link Closure}
     */
    static String asJsonString(Closure self) throws IOException {
        buildString(self, XContentType.JSON)
    }

    /**
     * Create {@code XContentBuilder} with the specified {@code type} containing the {@code Map}ped form of the
     * {@link Closure}.
     *
     * @param self The {@code this} reference for the {@link Closure}
     * @param type The type of {@code XContent} to create
     * @return Never {@code null}.
     * @throws NullPointerException if {@code self} is {@code null}
     * @throws org.elasticsearch.ElasticsearchIllegalArgumentException if {@code type} is unrecognized
     * @throws IOException if any error occurs while mapping the {@link Closure}
     */
    static XContentBuilder build(Closure self, XContentType type) throws IOException {
        XContentFactory.contentBuilder(type).map(self)
    }

    /**
     * Convert the {@link Closure} into a {@code Map}, and create {@code XContent} with the specified {@code type} as a
     * {@code byte[]}.
     *
     * @param self The {@code this} reference for the {@link Closure}
     * @param type The type of {@code XContent} to create in byte form
     * @return Never {@code null}.
     * @throws NullPointerException if {@code self} is {@code null}
     * @throws org.elasticsearch.ElasticsearchIllegalArgumentException if {@code type} is unrecognized
     * @throws IOException if any error occurs while mapping the {@link Closure}
     */
    static byte[] buildBytes(Closure self, XContentType type) throws IOException {
        build(self, type).bytes().toBytes()
    }

    /**
     * Convert the {@link Closure} into a {@code Map}, and create {@code XContent} with the specified {@code type} as a
     * {@code byte[]}.
     *
     * @param self The {@code this} reference for the {@link Closure}
     * @param type The type of {@code XContent} to create in byte form
     * @return Never {@code null}.
     * @throws NullPointerException if {@code self} is {@code null}
     * @throws org.elasticsearch.ElasticsearchIllegalArgumentException if {@code type} is unrecognized
     * @throws IOException if any error occurs while mapping the {@link Closure}
     */
    static String buildString(Closure self, XContentType type) throws IOException {
        build(self, type).string()
    }

    /**
     * Close the {@link XContentBuilder} and get the resulting {@link BytesReference} in the preset
     * {@code XContentType}.
     *
     * @param self The {@code this} reference for the {@link XContentBuilder}
     * @return Always {@link XContentBuilder#bytes()}.
     * @throws NullPointerException if {@code self} is {@code null}
     */
    static BytesReference getBytes(XContentBuilder self) {
        self.bytes()
    }

    /**
     * Close the {@link XContentBuilder} and get the result as a {@link BytesStream} in the preset {@code XContentType}.
     *
     * @param self The {@code this} reference for the {@link XContentBuilder}
     * @return Always {@link XContentBuilder#bytesStream()}.
     * @throws NullPointerException if {@code self} is {@code null}
     */
    static BytesStream getBytesStream(XContentBuilder self) {
        self.bytesStream()
    }

    /**
     * Get the internal {@link XContentGenerator} of the {@link XContentBuilder}.
     * <p />
     * Note: This does <em>not</em> close the {@link XContentBuilder#generator()}.
     *
     * @param self The {@code this} reference for the {@link XContentBuilder}
     * @return Always {@link XContentBuilder#generator()}.
     * @throws NullPointerException if {@code self} is {@code null}
     */
    static XContentGenerator getGenerator(XContentBuilder self) {
        self.generator()
    }


    /**
     * Get the internal {@link OutputStream} of the {@link XContentBuilder}.
     * <p />
     * Note: This does <em>not</em> close the {@link XContentBuilder#generator()}.
     *
     * @param self The {@code this} reference for the {@link XContentBuilder}
     * @return Always {@link XContentBuilder#stream()}.
     * @throws NullPointerException if {@code self} is {@code null}
     */
    static OutputStream getStream(XContentBuilder self) {
        self.stream()
    }

    /**
     * Close the {@link XContentBuilder} and get the result as a {@link String} in the preset {@code XContentType}.
     *
     * @param self The {@code this} reference for the {@link XContentBuilder}
     * @return Always {@link XContentBuilder#string()}.
     * @throws NullPointerException if {@code self} is {@code null}
     */
    static String getString(XContentBuilder self) {
        self.string()
    }

    /**
     * Convert the {@code closure} into a {@link Map} and call {@link XContentBuilder#field(String, Map)}, effectively
     * setting the {@code name} to the defined sub-object.
     *
     * @param self The {@code this} reference for the {@link XContentBuilder}
     * @param closure The closure to convert to a map and add to the builder
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     * @throws IOException if any error occurs while adding the field
     */
    static XContentBuilder field(XContentBuilder self, String name, Closure closure) throws IOException {
        self.field(name, closure.asMap())
    }

    /**
     * Convert the {@code closure} into a {@link Map} and call {@link XContentBuilder#field(String, Map)}, effectively
     * setting the {@code name} to the defined sub-object.
     *
     * @param self The {@code this} reference for the {@link XContentBuilder}
     * @param closure The closure to convert to a map and add to the builder
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     * @throws IOException if any error occurs while adding the field
     */
    static XContentBuilder field(XContentBuilder self, XContentBuilderString name, Closure closure) throws IOException {
        self.field(name, closure.asMap())
    }

    /**
     * Convert the {@code closure} into a {@link Map} and {@link XContentBuilder#map} it to {@code self}.
     *
     * @param self The {@code this} reference for the {@link XContentBuilder}
     * @param closure The closure to convert to a map and add to the builder
     * @return Always {@code self}.
     * @throws NullPointerException if any parameter is {@code null}
     * @throws IOException if any error occurs while adding the map
     */
    static XContentBuilder map(XContentBuilder self, Closure closure) throws IOException {
        self.map(closure.asMap())
    }
}
