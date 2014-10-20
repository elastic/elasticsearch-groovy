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
package org.elasticsearch.groovy

/**
 * {@code ClosureExtensions} adds convenient behaviors to Groovy's {@link Closure} class, such as the ability to convert
 * a {@code Closure} into a {@link Map} with {@link String} keys and {@link Object} values.
 */
class ClosureExtensions {
    /**
     * Convert the self-referenced {@link Closure} into a {@link Map} with {@link String} keys as {@link Object} values.
     * <pre>
     * Map&lt;String, Object&gt; closureMap = {
     *   name {
     *     first = "firstName"
     *     last = "lastName"
     *   }
     *   nested {
     *     object {
     *       property = "value"
     *     }
     *   }
     * }.asMap()
     * </pre>
     *
     * @param self The {@code this} reference for the {@link Closure}
     * @return Never {@code null}. Can be {@link Map#isEmpty() empty}.
     * @throws NullPointerException if {@code self} is {@code null}
     * @see ClosureToMapConverter#mapClosure(Closure)
     */
    static Map<String, Object> asMap(Closure self) {
        ClosureToMapConverter.mapClosure(self)
    }
}
