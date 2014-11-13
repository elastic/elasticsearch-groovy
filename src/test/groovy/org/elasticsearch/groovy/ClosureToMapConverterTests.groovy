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

import org.elasticsearch.test.ElasticsearchTestCase

import org.junit.Test

import static org.elasticsearch.groovy.ClosureToMapConverter.mapClosure

/**
 * Tests {@link ClosureToMapConverter}.
 */
class ClosureToMapConverterTests extends ElasticsearchTestCase {
    @Test
    void testFlatMapConversion() {
        long value = randomLong()
        String string = randomAsciiOfLengthBetween(1, 16)
        Date now = new Date()
        boolean bool = randomBoolean()

        Map<String, Object> map = mapClosure {
            id = value
            name = string
            date = now
            valid = bool
            // ensure methods work
            method = randomBoolean()
        }

        assert map.id == value
        assert map.name == string
        assert map.date == now
        assert map.valid == bool
        assert map.method instanceof Boolean
    }

    @Test
    void testReusedVariableMapConversion() {
        long value = randomLong()
        String string = randomAsciiOfLengthBetween(1, 16)
        Date now = new Date()
        boolean bool = randomBoolean()

        Closure closure = {
            id = value
            name = string
            date = now
            valid = bool
        }

        Map<String, Object> map = mapClosure(closure)

        assert map.id == value
        assert map.name == string
        assert map.date == now
        assert map.valid == bool

        value = randomLong()
        string = randomAsciiOfLengthBetween(1, 16)
        now = new Date()
        bool = ! bool

        map = mapClosure(closure)

        assert map.id == value
        assert map.name == string
        assert map.date == now
        assert map.valid == bool
    }

    @Test
    void testListMapConversion() {
        long value = randomLong()
        List values = [randomAsciiOfLengthBetween(1, 8), randomLong(), randomBoolean()]

        Map<String, Object> map = mapClosure {
            id = value
            list = values
        }

        assert map.id == value
        assert map.list == values
    }

    @Test
    void testNestedPropertyMapConversion() {
        long value = randomLong()
        String firstName = randomAsciiOfLengthBetween(1, 8)
        String lastName = randomAsciiOfLengthBetween(1, 8)
        Date now = new Date()
        List values = [randomAsciiOfLengthBetween(1, 8), randomLong(), randomBoolean()]

        Map<String, Object> map = mapClosure {
            id = value
            name = {
                first = firstName
                last = lastName
            }
            date = now
            list = values
        }

        assert map.id == value
        assert map.name instanceof Map
        assert map.name.first == firstName
        assert map.name.last == lastName
        assert map.date == now
        assert map.list == values
        // ensure there is no double-coverage
        assert map.first == null
        assert map.last == null
    }

    @Test
    void testNestedMethodMapConversion() {
        long value = randomLong()
        String firstName = randomAsciiOfLengthBetween(1, 8)
        String lastName = randomAsciiOfLengthBetween(1, 8)
        Date now = new Date()
        List values = [randomAsciiOfLengthBetween(1, 8), randomLong(), randomBoolean()]

        Map<String, Object> map = mapClosure {
            id value
            name {
                first firstName
                last lastName
            }
            date now
            list values
        }

        assert map.id == value
        assert map.name instanceof Map
        assert map.name.first == firstName
        assert map.name.last == lastName
        assert map.date == now
        assert map.list == values
        // ensure there is no double-coverage
        assert map.first == null
        assert map.last == null
    }

    @Test
    void testComplexMapConversion() {
        long value = randomLong()
        String firstName = randomAsciiOfLengthBetween(1, 8)
        String lastName = randomAsciiOfLengthBetween(1, 8)
        Date start = new Date(randomLong())
        Date now = new Date()
        double number = randomDouble()
        List methodValues = [randomInt(), randomFloat()]
        List values = [randomAsciiOfLengthBetween(1, 8), randomLong(), randomBoolean()]
        int[] ints = [randomInt(), randomInt()] as int[]
        Map mapValues = [key : randomInt()]

        Set setValues = [] as Set
        int setSize = randomInt(8)

        // random number of values
        for (int i = 0; i < setSize; ++i) {
            setValues.add(randomDouble())
        }

        Map<String, Object> map = mapClosure {
            id = value
            user {
                name {
                    first = firstName
                    middle = [randomAsciiOfLengthBetween(1, 8), randomAsciiOfLengthBetween(1, 8)]
                    last = lastName
                }
                dates = {
                    // same fieldname as name
                    first = start
                }
            }
            percent = number
            collections {
                list = values
                map = mapValues
                // will be converted to a list:
                set = setValues
            }
            timestamp = now
            // single method invocation with two parameters:
            method methodValues[0], methodValues[1]
            array randomLong(), randomDouble()
            intArray ints
        }

        assert map.id == value
        assert map.user.name.first == firstName
        assert map.user.name.middle.size() == 2
        assert map.user.name.last == lastName
        assert map.user.dates.first == start
        assert map.percent == number
        assert map.collections.list == values
        assert map.collections.map == mapValues
        assert map.collections.set == setValues.collect()
        assert map.timestamp == now
        assert map.method == methodValues
        assert map.array instanceof List
        assert map.array.size() == 2
        assert map.intArray instanceof List
        assert map.intArray == ints as List
        // observed while developing, so this ensures that it isn't being defined
        assert map.user.randomAsciiOfLengthBetween == null
    }
}
