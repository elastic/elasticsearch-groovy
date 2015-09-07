Groovy Client for Elasticsearch
===============================

The Elasticsearch Groovy client project helps you to use Elasticsearch in Groovy projects. This Groovy client inherently
supports 100% of the Elasticsearch API for the supported version by using Groovy extension modules with the Java client.
Literally anything possible in the same version of the Java client is possible with the Groovy client, plus some
Groovy-friendly extensions.

You can use the Java client code from any Java client example that you find online _with the benefit of the Groovy
extensions_.

```groovy
TransportClient client = new TransportClient(ImmutableSettings.settingsBuilder {
  client.transport.sniff = true
  cluster.name = "your-cluster-name"
})

// identical to the Java client:
client.addTransportAddress( ... )

String userId = "some-user-id"

// asynchronously fetch the results
ListenableActionFuture<SearchResponse> future = client.search {
  indices "your-index"
  types "your-type"
  source {
    query {
      match {
        user.id = userId
      }
    }
  }
}

// block until the response is retrieved (you could alternatively use listeners)
SearchResponse response = future.actionGet()
```

Besides the usage of `Closure`s, the above example should look very familiar to any existing Java client users, as well
as those familiar with the Elasticsearch DSL (Domain Specific Language used for indexing and querying).

Versions
--------

In general, the version number will match the release of Elasticsearch.

|    Elasticsearch    |     Groovy Client           |    Java       | Groovy |
|---------------------|-----------------------------|---------------|--------|
| 2.0.0-beta1         | 2.0.0-beta1-SNAPSHOT        | 7u60 or later | 2.4.4  |

To build a `SNAPSHOT` version, you need to build it with Gradle (see below for further details):

```bash
$ gradle clean installDist
```

This is particularly relevant on the 2.x and master branches, which do make occasional snapshot releases, but they may
be behind the most up-to-date snapshot release(s). In general, this is not a concern due to the way that the Groovy
client is written using Groovy Extensions, but non-backwards compatible changes can still break those too.

Groovy Warning
--------------

Groovy released Groovy 2.4.4 to fix a vulnerability with [CVE-2015-3253](http://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2015-3253).

You are considered vulnerable just by having an earlier version of Groovy on your classpath! All users should upgrade
to Groovy 2.4.4 as a result.

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
  compile 'org.elasticsearch:elasticsearch-groovy:2.0.0-beta1'
}
```

### Maven

```xml
<dependencies>
  <dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch-groovy</artifactId>
    <version>2.0.0-beta1</version>
    <scope>compile</scope>
  </dependency>
</dependencies>
```

### Grails 2.x

Out-of-the-box support for Grails is limited to Grails 2.4.4 or later. To use with earlier versions,
[you must follow the instructions found in GRAILS-10652](https://jira.grails.org/browse/GRAILS-10652)
to load Groovy extension modules.

```gradle
repositories {
  mavenCentral()
}

dependencies {
  // You may be able to use the 'runtime' scope
  compile group: 'org.elasticsearch', name: 'elasticsearch-groovy', version: '2.0.0-beta1', classifier: 'grails'
}
```

Grails 3.x has overhauled their build system to use Gradle, which makes it easy for you to select the
version of Groovy to use with it, including the use of `invokedynamic`. However, Grails 2.x did not
make it easy. A part of not being easy, Grails 2.x does not use the `invokedynamic`-compatible Groovy
jar, which means that any Grails 2.x project requires a jar that is not compiled with `invokedynamic`.

With the release of Elasticsearch Groovy 1.4.3, we have introduced a secondary jar with a new `grails`
_classifier_ that can be used by Grails users. All other users are _strongly_ recommended to use
the `invokedynamic`-compatible versions described above (it's both faster and slightly smaller!).

Support for this is intended to assist the Grails community to use the Elasticsearch Groovy client
prior to the release of Grails 3.0. If you are using Grails 3.0 or later, then you should use the
`invokedynamic` version of Groovy and the Gradle dependency above.

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
  compile 'org.codehaus.groovy:groovy-all:2.4.4:indy'
}
```

#### Maven

```xml
<dependencies>
  <dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-all</artifactId>
    <version>2.4.4</version>
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

    Copyright 2009-2015 Elastic <http://www.elastic.co>

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy of
    the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
    License for the specific language governing permissions and limitations under
    the License.