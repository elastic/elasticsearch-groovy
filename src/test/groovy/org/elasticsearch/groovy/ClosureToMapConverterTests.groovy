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

import org.junit.Test

import static org.elasticsearch.groovy.ClosureToMapConverter.mapClosure

/**
 * Tests {@link ClosureToMapConverter}.
 */
class ClosureToMapConverterTests extends AbstractESTestCase {
    /**
     * A random {@code long} value.
     * <p>
     * This is specifically called out as a local field to ensure that the {@link Closure} properly uses the field via
     * delegation.
     */
    private long value = randomLong()

    /**
     * "Short Hand" means that we are using using the full property name rather than nesting closures. It's the
     * difference between
     * <pre>
     * {
     *   x.y.z = 123
     * }
     * </pre>
     * and
     * <pre>
     * {
     *   x {
     *     y {
     *       z = 123
     *     }
     *   }
     * }
     * </pre>
     * Using the short hand form is <em>not</em> allowed on the right hand side (it adds a lot of unnecessary
     * complexity).
     */
    @Test(expected = IllegalArgumentException)
    void testShortHandOnRightHandSide_Fails() {
        mapClosure {
            x.y.z = randomInt()
            // NOT ALLOWED:
            c = x.y.z     // <--- This is not allowed!
        }
    }

    @Test(expected = MissingPropertyException)
    void testShortHand_FailsWithBadOrder() {
        mapClosure {
            // Note: You define 'x' to be an int
            x = randomInt()
            // Now we try to define 'x.y.z', but 'x' already exists so it gets returned; then it tries to find 'y' as
            //  a property of 'x', which won't exist
            x.y.z = randomInt()
        }
    }

    @Test
    void testShortHandValid() {
        Map<String, Object> values = [key: randomInt()]

        // NOTE: The keys are intentionally ordered. The testShortHand_FailsWithBadOrder test will try the other order
        Map<String, Object> map = mapClosure {
            a.b.c = value
            a = value     // Proper way to reuse the value from a shorthand property

            b.c.d = values.key
            b = values.key
        }

        assert map['a.b.c'] == value
        assert map.a == value
        assert map['b.c.d'] == values.key
        assert map.b == values.key
    }

    @Test
    void testFlatMapConversion() {
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
    void testFlatNestedConversion() {
        String string = randomAsciiOfLengthBetween(1, 16)
        Date now = new Date()
        boolean bool = randomBoolean()
        Map<String, Object> checkMap = [key1: randomInt(), key2: [inner: randomInt()]]

        // This is necessary to handle fields with "." in the name
        Map<String, Object> map = mapClosure {
            date = now
            // Ensure nested field names work
            object1.id = value
            // Ensure nested-nested
            object2.object3.name = string
            // Ensure nested-nested-nested...
            object4.object5.object6.valid = bool
            // Ensure that right-hand side is parsed properly
            object7.key1 = checkMap.key1
            object8.key2 = checkMap.key2.inner
        }

        assert map.date == now
        // NOTE: You _must_ access the keys as strings because it does _not_ translate
        // otherwise
        assert map['object1.id'] == value
        assert map['object2.object3.name'] == string
        assert map['object4.object5.object6.valid'] == bool
        assert map['object7.key1'] == checkMap.key1
        assert map['object8.key2'] == checkMap.key2.inner

    }

    @Test
    void testReusedVariableMapConversion() {
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
