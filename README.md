Groovy Client for Elasticsearch
===============================

The Elasticsearch Groovy Client project helps you to use Elasticsearch in Groovy projects. This Groovy client is
different from previous releases in that it inherently supports 100% of the Elasticsearch API for the supported version
by using the Groovy extension feature with the Java Client. Literally anything possible in the same version of the Java
Client is possible with the Groovy client, plus some Groovy-friendly extensions.

In much earlier versions of the client, you would run code like this:

```groovy
GClient client = new GNodeBuilder().settings { ... }.build().client
GActionFuture future = client.index { ... }.gexecute()
```

This would provide a `GClient` from a `GNodeBuilder`, which was meant to provide the same features as the Java Client's
`Client` and `NodeBuilder`. However, because they had to be written to support every new feature of the Java Client, it
was missing some method variants.

In this release, all of the `G`-prefixed classes have been replaced by extensions, which means that you can now use the
Java Client code from any Java Client example that you find online <em>with the benefit of the Groovy extensions</em>.

```groovy
Client client = nodeBuilder().settings { ... }.build().client
ListenableActionFuture<IndexResponse> future = client.index { ... }.actionGet()
```

Besides the usage of `Closure`s, the above example should look very familiar to existing Java Client users.

Versions
--------

|     Groovy Client           |    elasticsearch    |  groovy  | Release date |
|-----------------------------|---------------------|----------|:------------:|
| 1.3.4.0-SNAPSHOT            | 1.3.4               |  2.3.2   |  XXXX-XX-XX  |

Please read documentation relative to the version you are using:

* [1.3.4.0-SNAPSHOT](https://github.com/elasticsearch/elasticsearch-groovy/blob/master/README.md)

Adding to your Groovy projects
------------------------------

### Gradle

```gradle
repositories {
    mavenCentral()
}

dependencies {
    compile 'org.elasticsearch:elasticsearch-groovy:1.3.4.0-SNAPSHOT'
}
```

### Maven

```
<dependencies>
  <dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch-groovy</artifactId>
    <version>1.3.4.0-SNAPSHOT</version>
    <scope>compile</scope>
  </dependency>
</dependencies>
```

Compiling Groovy Client
-----------------------

To compile this code on your own, then run:

```bash
$ mvn clean package -DskipTests
```

In the longer term, there will be a Gradle build script to perform this action.

Testing Groovy Client
---------------------

The Groovy Client makes use of the [Randomized Testing framework used by Elasticsearch
itself](http://www.elasticsearch.org/blog/elasticsearch-testing-qa-increasing-coverage-randomizing-test-runs/). The unit
tests and integration tests that this uses can be invoked with the same command:

```bash
$ mvn clean test
```

In the longer term, there will be a Gradle build script to perform this action.

Suggested Groovy Settings
-------------------------

Since the release of Java 7 (aka Java 1.7), higher level languages like Groovy have had access to the [`invokedynamic`
JVM instruction]((http://groovy.codehaus.org/InvokeDynamic+support)). This avoids the need for some runtime code
generation (e.g., `$callSiteArray`s) and it theoretically speeds up all Groovy code. In the Groovy world, there is still
support for Java 5 and Java 6, which means that `invokedynamic` cannot be enabled by default.

### Compiling Groovy with `invokedynamic` support

To support `invokedynamic` in your own Groovy project(s), at a minimum, you *must* include the `invokedynamic`-compiled
Groovy jar, which Codehaus calls the `indy` (`in`voke`dy`namic) jar.

#### Gradle

```gradle
repositories {
    mavenCentral()
}

dependencies {
    compile 'org.codehaus.groovy:groovy-all:2.3.2:indy'
}
```

#### Maven

```
<dependencies>
  <dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-all</artifactId>
    <version>2.3.2</version>
    <classifier>indy</classifier>
    <scope>compile</scope>
  </dependency>
</dependencies>
```

### Using `invokedynamic` in _your_ Groovy code

After including the `indy` jar, you now _only_ have an `invokedynamic`-compatible Groovy runtime. All internal Groovy
calls will use `invokedynamic`, as will any other Groovy code compiled with `invokedynamic` support (e.g., the
Elasticsearch Groovy Client), but _your_ code must also be compiled with `invokedynamic` support.

#### Gradle

```gradle
apply plugin: 'groovy'

// ...

/**
 * Customize Groovy compilation.
 */
tasks.withType(GroovyCompile) {
  groovyOptions.optimizationOptions.indy = true
}
```

#### Maven

Maven has numerous ways to do this, and it largely depends on how you compile your Groovy code. If you are wrapping the
Ant task, then add `indy="true"` to the Groovy compilation. Otherwise check your plugin's documentation.

#### IntelliJ

When allowing IntelliJ to control the compilation of your project, then you must enable `Invoke dynamic support` within
the preferences for the `Groovy Compiler`.

To change this setting:

1. Open `Preferences`
2. Select `Compiler`
3. Select `Groovy Compiler`
4. Check `Invoke dynamic support`

JVM Warning
-----------

Both Elasticsearch and the Elasticsearch Groovy Client require Java 7. In addition, the Groovy Client is compiled with
`indy` support enabled, which means that it theoretically could cause issues if you are running with Java 7u22 to 7u55 due
to a bug in the JVM related to `invokedynamic`. This is [reported directly from the Groovy
developers](http://groovy.codehaus.org/InvokeDynamic+support) and it is strongly suggested that you run Java 7u60 or
later.

|     JVM Release    | Groovy Client Support | `invokedynamic` Support |
|--------------------|-----------------------|-------------------------|
| Java 5             | *Unsupported*         | None                    |
| Java 6             | *Unsupported*         | None                    |
| Java 7u22 to 7u55  | *Unsupported*         | **Buggy**               |
| Java 7u60 or later | Supported             | Supported               |

License
-------

    This software is licensed under the Apache 2 license, quoted below.

    Copyright 2009-2014 Elasticsearch <http://www.elasticsearch.org>

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy of
    the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
    License for the specific language governing permissions and limitations under
    the License.
