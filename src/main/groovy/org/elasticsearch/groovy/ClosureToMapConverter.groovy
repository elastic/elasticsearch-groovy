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
 * <p>
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
        mapClosureWithOwner(closure, closure.owner)
    }

    /**
     * Convert the {@code closure} into a {@link Map}.
     * <p>
     * The {@code owner} comes up when users specify variables within the {@link Closure}.
     *
     * @param closure The closure to convert.
     * @Param owner The owner of the {@code closure}.
     * @return Never {@code null}. Can be {@link Map#isEmpty() empty}.
     */
    private static Map<String, Object> mapClosureWithOwner(Closure closure, Object owner) {
        new ClosureToMapConverter(closure, owner).convert()
    }

    /**
     * The "buildName" is used to maintain state for names like "field.innerField.nested", which
     * is actually 3 separate fields without recognizing it.
     * <pre>
     * Map&lt;String, Object&gt; map = mapClosure {
     *   field.innerField.nested = value
     * }
     * </pre>
     * This allows us to parse the field name as literally "field.innerField.nested" by tracking requests
     * for the field "field", followed by "innerField" and finally attempting to set "nested".
     * <p>
     * This format should <em>only</em> be used when for things like changing settings or searching (e.g.,
     * a match request against an inner field or a nested query).
     */
    private String buildName = null
    /**
     * The unraveled {@link Closure} after calling {@link #convert()}.
     */
    private final Map<String, Object> map = [:]
    /**
     * The closure that is unraveled into the {@link #map}.
     */
    final Closure closure
    /**
     * The owner of the root {@link #closure}.
     * <p>
     * Inner {@link Closure}s handle their owner differently than the root one, so it's important to track the root's
     * owner.
     */
    final Object rootOwner

    /**
     * Construct a new {@link ClosureToMapConverter} that delegates the {@code closure} to call the constructed
     * instance ({@code this}) when it is invoked. These calls are used to unravel the {@code closure} into the
     * {@link #map}.
     *
     * @param closure The {@link Closure} to convert into a {@link Map}.
     * @throws NullPointerException if {@code closure} is {@code null}
     */
    private ClosureToMapConverter(Closure closure, Object owner) {
        // required
        this.closure = (Closure)closure.clone()
        this.rootOwner = owner

        // When looking up properties and invoking methods, it first looks at the delegate (THIS) for the value. If
        //  the delegate (THIS) does not have it, then it will check the owner for the value (effectively the closure).
        this.closure.delegate = this
        // Note: Using OWNER_FIRST (the default) does not work except for non-nested closures.
        //       This class will take it over entirely, as the delegate, for property access, but not method invocation.
        this.closure.resolveStrategy = Closure.DELEGATE_FIRST
    }

    /**
     * Trigger the conversion of the {@link #closure} to the {@link #map}.
     * <p>
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
     * <p>
     * This method has a side-effect <em>if</em> you are requesting a {@code propertyName} that is unrecognized to both
     * {@code this} (its internal {@link #map}) or the {@link #rootOwner}. In that case, the this method will remember
     * the {@code propertyName} for future use with {@link #setProperty} or {@link #invokeMethod}.
     * <p>
     * The reason that it keeps track of this the {@code propertyName} is because this method is only called when a
     * value is sought. In general, that only occurs on the <em>right hand side</em> (e.g., x = y, where y is the right
     * hand side). However, if the value is unrecognized, then it's either an error <em>or</em>, more likely, it's being
     * used in the form of "x.y.z = a" rather than "x { y { z = a } }". Doing this should is considered an error.
     * <pre>
     * Map&lt;String, Object&gt; map = mapClosure {
     *   x.y.z = 123
     *   a = x.y.z     // BAD!!
     * }
     * </pre>
     * If you really need similar functionality, then define a temporary variable <em>outside</em> of the
     * {@code Closure} and use it.
     * <pre>
     * int value = 123
     *
     * Map&lt;String, Object&gt; map = mapClosure {
     *   x.y.z = value
     *   a = value     // GOOD!!
     * }
     * </pre>
     * <p>
     * Perhaps unexpectedly, this will <em>never</em> return property values unless given the full name. When Groovy
     * is unraveling the shorthand version, this method is called twice in the above example. Once with "x", again with
     * "y", and finally the setter is called against "y" for "z". By tracking "x" and "y", we can appropriately create
     * the "x.y.z" key that is intended. Because we are building it, we don't want to return any value that we happen
     * across "along the way" because each key in this format is effectively not associated with others.
     */
    @Override
    Object getProperty(String propertyName) {
        Object returned = this

        // if we know about it, then return the value
        if (map.containsKey(propertyName)) {
            returned = map[propertyName]
        }
        // NOTE: The following blocks are here to handle the less common "key1.key2" on the left-hand side
        //       This is meant to be less common
        // if we don't know about it, then start building the actual key name
        //  (key is in form of "key1.key2.key3" and this will be "key1"
        else if (buildName == null) {
            // If the OWNER of the closure has the value, then we want to use that value because it will get assigned.
            // NOTE: This "owner" is only the same as "closure.owner" for the root closure.
            if (rootOwner.hasProperty(propertyName)) {
                // the owner has it, so just return that value (this _should_ be on the right hand side used as a value)
                returned = rootOwner.getProperty(propertyName)
            }
            else {
                buildName = propertyName
            }
        }
        // continuation from above where it's now "key2"; this method would never get "key3" unless it was on the
        else {
            buildName += '.' + propertyName
        }

        returned
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
        String fullName = propertyName

        // If we are maintaining state trying to load the name
        if (buildName != null) {
            fullName = buildName + "." + propertyName
            // We just now saw "key3", so we can throw away the name now
            buildName = null
        }

        map[fullName] = convertValue(newValue)
    }

    /**
     * Convert the passed in {@code value} into an object that is not a {@link Closure}, and convert any
     * {@link Collection}s into {@link List}s.
     * <p>
     * This will recursively call itself as necessary.
     *
     * @param value The incoming parameter to convert if necessary ({@link Closure}s and {@link Collection}s)
     * @return {@code value} as-is unless it is a {@link Closure} or {@link Collection}. Otherwise the unraveled
     *         value of those objects.
     * @throws IllegalArgumentException if you attempt to reuse a shorthand property on the right hand side (e.g.,
     *                                  "x.y.z = a; b = x.y.z;" where "x.y.z" is the shorthand property)
     */
    private Object convertValue(Object value) {
        Object ret = value

        // avoid handling shorthand assignments (technically we could check for this, then build the name here, but
        //  this is a very bad code smell; just use a temporary variable!)
        if (value instanceof ClosureToMapConverter) {
            throw new IllegalArgumentException(
                    "value is a ClosureToMapConverter. This means that you are trying to reuse a shorthand " +
                    "property! (For example, { x.y.z = 123; a = x.y.z }. 'x.y.z' cannot be referenced on the right " +
                    "hand side! Use a temporary variable from outside the closure instead.)")
        }
        // enable nested objects
        else if (value instanceof Closure) {
            // avoid overwriting this instance's map; maintain the owner!
            ret = mapClosureWithOwner(value, rootOwner)
        }
        // unravel collections into a List
        else if (value instanceof Collection) {
            // use _this_ method by passing its method address to be invoked
            ret = ((Collection)value).collect(this.&convertValue)
        }
        // unravel arrays into a List (note: this hits native arrays like int[])
        else if (value instanceof Object[] || (value != null && value.getClass().isArray())) {
            ret = (value as List).collect(this.&convertValue)
        }

        ret
    }
}
