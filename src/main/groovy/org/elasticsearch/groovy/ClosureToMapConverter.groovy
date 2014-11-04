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
 * {@code ClosureToMapConverter} serves as a utility to convert {@link Closure}s into {@link Map}s with {@link String}
 * keys and {@link Object} values. The values can be {@link List}s, {@link Map}s, or other values (normal objects). In
 * general, this serves as a convenient way to load JSON-like syntax in Groovy and convert it into a format usable by
 * Elasticsearch.
 * <pre>
 * Map&lt;String, Object&gt; map = mapClosure {
 *   name {
 *     first = "Example"
 *     middle = ["First", "Second"]
 *     last = "Name"
 *   }
 *   details {
 *     nested {
 *       user_id = 1234
 *       timestamp = new Date()
 *     }
 *   }
 * }
 * </pre>
 * This class is added to {@link Closure}s automatically via the {@link ClosureExtensions} class, so the above code can
 * be slightly simplified by avoiding the static import of {@link ClosureToMapConverter#mapClosure} and instead using
 * the {@link ClosureExtensions#asMap(Closure)} extension method.
 * <pre>
 * Map&lt;String, Object&gt; map = {
 *   name {
 *     first = "Example"
 *     middle = ["First", "Second"]
 *     last = "Name"
 *   }
 *   details {
 *     nested {
 *       user_id = 1234
 *       timestamp = new Date()
 *     }
 *   }
 * }.asMap()
 * </pre>
 * It's important to note that the field name's can be specified as methods <em>or</em> properties. This means that
 * <pre>
 * {
 *   name {
 *     first "Example"
 *   }
 * }
 * </pre>
 * is treated the same way as
 * <pre>
 * {
 *   name = {
 *     first = "Example"
 *   }
 * }
 * </pre>
 * Mixing the two styles is allowed, but consistency is naturally very important for code readability.
 * <p />
 * Instances should never be reused. Instances of this class are not thread safe, but separate instances do not share
 * any state and therefore multiple instances can run in parallel.
 * @see ClosureExtensions#asMap(Closure)
 */
class ClosureToMapConverter {
    /**
     * Convert the {@code closure} into a {@link Map}.
     *
     * @param closure The closure to convert.
     * @return Never {@code null}. Can be {@link Map#isEmpty() empty}.
     */
    static Map<String, Object> mapClosure(Closure closure) {
        new ClosureToMapConverter(closure).convert()
    }

    /**
     * Convert the passed in {@code value} into an object that is not a {@link Closure}, and convert any
     * {@link Collection}s into {@link List}s.
     * <p />
     * This will recursively call itself as necessary.
     *
     * @param value The incoming parameter to convert if necessary ({@link Closure}s and {@link Collection}s)
     * @return {@code value} as-is unless it is a {@link Closure} or {@link Collection}. Otherwise the unraveled
     *         value of those objects.
     */
    private static Object convertValue(Object value) {
        Object ret = value

        // enable nested objects
        if (value instanceof Closure) {
            // avoid overwriting this instance's map
            ret = mapClosure(value)
        }
        // unravel collections into a List
        else if (value instanceof Collection) {
            // use _this_ method by passing its method address to be invoked
            ret = ((Collection)value).collect(ClosureToMapConverter.&convertValue)
        }
        // unravel arrays into a List (note: this hits native arrays like int[])
        else if (value instanceof Object[] || (value != null && value.getClass().isArray())) {
            ret = (value as List).collect(ClosureToMapConverter.&convertValue)
        }

        ret
    }

    /**
     * The unraveled {@link Closure} after calling {@link #convert()}.
     */
    private final Map<String, Object> map = [:]
    /**
     * The closure that is unraveled into the {@link #map}.
     */
    final Closure closure

    /**
     * Construct a new {@link ClosureToMapConverter} that delegates the {@code closure} to call the constructed
     * instance ({@code this}) when it is invoked. These calls are used to unravel the {@code closure} into the
     * {@link #map}.
     *
     * @param closure The {@link Closure} to convert into a {@link Map}.
     * @throws NullPointerException if {@code closure} is {@code null}
     */
    private ClosureToMapConverter(Closure closure) {
        // required
        this.closure = (Closure)closure.clone()

        // When looking up properties and invoking methods, it first looks at the delegate (THIS) for the value. If
        //  the delegate (THIS) does not have it, then it will check the owner for the value (effectively the closure).
        this.closure.delegate = this
        // Note: Using OWNER_FIRST (the default) does not work except for non-nested closures.
        this.closure.resolveStrategy = Closure.DELEGATE_FIRST
    }

    /**
     * Trigger the conversion of the {@link #closure} to the {@link #map}.
     * <p />
     * This method should only be invoked once.
     *
     * @return Never {@code null}. {@link Map#isEmpty() Empty} if the {@code closure} does not assign any properties.
     */
    Map<String, Object> convert() {
        // invoke the closure, thus triggering calls to the delegate (this)
        closure.call()

        map
    }

    /**
     * Called when the {@link #closure} is delegating to {@code this} instance for method invocations. For example
     * <pre>
     * { username "kimchy" }
     * </pre>
     * The above {@link Closure} would pass a {@code methodName} set to "username" and {@code args} as "kimchy"
     * within a single element {@code Object[]}.
     * <pre>
     * {
     *   user {
     *     id 1234
     *     name "kimchy"
     *   }
     * }
     * </pre>
     * This {@link Closure} would pass a {@code methodName} set to "user" and {@code args} as the user closure within
     * a single element {@code Object[]}. A nested invocation of that closure would pass a {@code methodName} of
     * "id" and {@code args} as 1234 within a single element {@code Object[]}. A separate nested invocation of that
     * closure would handle the "name" field.
     */
    @Override
    Object invokeMethod(String methodName, Object args) {
        Object value = args

        // single element arrays are the most expected form:
        // Map map = {
        //   name {
        //     value = "xyz"
        //   }
        // }
        // methodName would be "name" and args would be the closure
        if (args instanceof Object[] && args.size() == 1) {
            value = args[0]
        }

        // assign the value of the would-be method
        setProperty(methodName, value)

        // return the value
        getProperty(methodName)
    }

    /**
     * Get the value defined in the {@link #map} with the {@code propertyName}.
     */
    @Override
    Object getProperty(String propertyName) {
        map[propertyName]
    }

    /**
     * Called when the {@link #closure} is delegating to {@code this} instance. For example
     * <pre>
     * { username = "kimchy" }
     * </pre>
     * The above {@link Closure} would pass a {@code propertyName} set to "username" and a {@code newValue} as "kimchy".
     */
    @Override
    void setProperty(String propertyName, Object newValue) {
        map[propertyName] = convertValue(newValue)
    }
}
