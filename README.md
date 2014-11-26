Groovy Client for Elasticsearch
===============================

The Elasticsearch Groovy client project helps you to use Elasticsearch in Groovy projects. This Groovy client is
different from previous releases in that it inherently supports 100% of the Elasticsearch API for the supported version
by using the Groovy extension feature with the Java client. Literally anything possible in the same version of the Java
client is possible with the Groovy client, plus some Groovy-friendly extensions.

In much earlier versions of the client, you would run code like this:

```groovy
GClient client = new GNodeBuilder().settings { ... }.node().client
GActionFuture future = client.index { ... }.gexecute()
```

This would provide a `GClient` from a `GNodeBuilder`, which was meant to provide the same features as the Java client's
`Client` and `NodeBuilder`. However, because they had to be written to support every new feature of the Java client, it
was missing some method variants.

In this release, all of the `G`-prefixed classes have been replaced by extensions, which means that you can now use the
Java client code from any Java client example that you find online <em>with the benefit of the Groovy extensions</em>.

```groovy
Client client = nodeBuilder().settings { ... }.node().client
ListenableActionFuture<IndexResponse> future = client.index { ... }
```

Besides the usage of `Closure`s, the above example should look very familiar to existing Java client users.

Versions
--------

You need to install a version matching your Elasticsearch version:

|    Elasticsearch    |     Groovy Client           |    Java       | Groovy |
|---------------------|-----------------------------|---------------|--------|
| master              | Build from source           | See below     | 2.3.7  |
| 1.x                 | Build from source           | 7u60 or later | 2.3.7  |
| 1.4                 | [1.4](https://github.com/elasticsearch/elasticsearch-groovy/tree/1.4) | 7u60 or later | 2.3.7  |

Please read documentation relative to the version that you are using!

To build a SNAPSHOT version, you need to build it with Gradle (see below for further details):

```bash
$ gradle clean installDist
```

JVM Warning
-----------

Both Elasticsearch and the Elasticsearch Groovy client require at least Java 7. In addition, the Groovy client is
compiled with `indy` support enabled, which means that it theoretically could cause issues if you are running with Java
7u22 to 7u55 due to a bug in the JVM related to `invokedynamic`. This is [reported directly from the Groovy
developers](http://groovy.codehaus.org/InvokeDynamic+support) and it is strongly suggested that you run Java 7u60 or
later.

|     JVM Release    | Groovy Client Support | `invokedynamic` Support |
|--------------------|-----------------------|-------------------------|
| Java 5             | *Unsupported*         | None                    |
| Java 6             | *Unsupported*         | None                    |
| Java 7u22 to 7u55  | *Unsupported*         | **Buggy**               |
| Java 7u60 or later | Supported             | Supported               |
| Java 8             | Supported             | Supported               |

Groovy is supported on any JDK supported by Elasticsearch, which currently includes Oracle JDK and OpenJDK.

Adding to your Groovy projects
------------------------------

### Gradle

```gradle
repositories {
    mavenCentral()
}

dependencies {
    compile 'org.elasticsearch:elasticsearch-groovy:1.4.1'
}
```

### Maven

```
<dependencies>
  <dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch-groovy</artifactId>
    <version>1.4.1</version>
    <scope>compile</scope>
  </dependency>
</dependencies>
```

Compiling Groovy Client
-----------------------

To compile this code on your own, then run:

```bash
$ gradle clean installDist
```

This will skip all tests and place the compiled jar in
`./build/install/elasticsearch-groovy/elasticsearch-groovy-{version}.jar`. It will package all dependencies (e.g., 
`elasticsearch-{version}.jar`) into `./build/install/elasticsearch-groovy/lib`.

Testing Groovy Client
---------------------

The Groovy client makes use of the [Randomized Testing framework used by Elasticsearch
itself](http://www.elasticsearch.org/blog/elasticsearch-testing-qa-increasing-coverage-randomizing-test-runs/). The unit
tests and integration tests that this uses can be invoked with the same command:

```bash
$ gradle clean test
```

The various `tests.*` and `es.*` system properties that are used by Elasticsearch are also used by the Gradle build
script. As a result, any recommendation that suggests running `mvn clean test -DsystemProp=xyz` can be replaced with
`gradle clean test -DsystemProp=xyz` (the only change was from `mvn` to `gradle`). This _only_ applies to the Groovy
client.

### Testing with IntelliJ

By default, IntelliJ will place all of the `compile`-time dependencies above the `testCompile` dependencies. In the case
of the test frameworks used, this presents issues that _occasionally_ trigger test failures (that tell you to fix your
classpath with respect to "test-framework.jar"). To fix this behavior, put your test dependencies above any non-test
dependencies within IntelliJ.

1. Open `Project Structure`
2. Select `Modules`

Suggested Groovy Settings
-------------------------

Since the release of Java 7 (aka Java 1.7), higher level languages like Groovy have had access to the [`invokedynamic`
JVM instruction]((http://groovy.codehaus.org/InvokeDynamic+support)). This avoids the need for some runtime code
generation (e.g., `$callSiteArray`s) and it theoretically speeds up all Groovy code. In the Groovy world, there is still
support for Java 5 and Java 6, which means that `invokedynamic` cannot be enabled by default.

### Compiling Groovy with `invokedynamic` support

To support `invokedynamic` in your own Groovy project(s), at a minimum, you *must* include the `invokedynamic`-compiled
Groovy jar, which the Groovy developers call the `indy` (`in`voke`dy`namic) jar.

#### Gradle

```gradle
repositories {
    mavenCentral()
}

dependencies {
    compile 'org.codehaus.groovy:groovy-all:2.3.7:indy'
}
```

#### Maven

```
<dependencies>
  <dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-all</artifactId>
    <version>2.3.7</version>
    <classifier>indy</classifier>
    <scope>compile</scope>
  </dependency>
</dependencies>
```

### Using `invokedynamic` in _your_ Groovy code

After including the `indy` jar, you now _only_ have an `invokedynamic`-compatible Groovy runtime. All internal Groovy
calls will use `invokedynamic`, as will any other Groovy code compiled with `invokedynamic` support (e.g., the
Groovy client), but _your_ code must also be compiled with `invokedynamic` support to gain the benefits within your
compiled jar(s).

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
