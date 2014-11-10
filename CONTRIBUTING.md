Contributing to Elasticsearch Groovy Client
===========================================

Elasticsearch is an open source project and we love to receive contributions from our community — you!
There are many ways to contribute, from writing tutorials or blog posts, improving the documentation,
submitting bug reports and feature requests or writing code which can be incorporated into the 
Elasticsearch Groovy client itself.

Bug Reports
-----------

If you think you have found a bug in the Elasticsearch Groovy client, first make sure that you are
testing against the [latest version of Elasticsearch](http://www.elasticsearch.org/download/) and the
Groovy client - your issue may already have been fixed. If not, search our
[issues list](https://github.com/elasticsearch/elasticsearch-groovy/issues) on GitHub in case a similar
issue has already been opened. Do feel free to add to existing issues, but please consider that practically
everyone has different priorities.

It is very helpful if you can prepare a reproduction of the bug. In other words, provide a small test case which we can
run to confirm your bug. It makes it easier to find the problem and to fix it. Test cases should be provided as `curl` 
commands which we can copy and paste to run it locally, for example:

```groovy
// Using a node client
Client client = nodeBuilder().local(true).node()

// 1. delete the index
client.admin.indices.delete {
  indices "test"
}.actionGet()

// 2. insert a document
client.index {
  index "test"
  type "test"
  id "1"
  source {
    title = "test document"
  }
}

// 3. this should return XXXX but instead returns YYY
client....
```

Provide as much information as you can. You may think that the problem lies with your query, when actually it depends
on how your data is indexed. The easier it is for us to recreate your problem, the faster it is likely to be fixed.

Feature Requests
----------------

If you find yourself wishing for a feature that doesn't exist in the Elasticsearch Groovy client, you are probably not
alone. There are bound to be others out there with similar needs.

Many of the features that [Elasticsearch](https://github.com/elasticsearch/elasticsearch) has today have been added
because our users saw the need. Open an issue on our [issues
list](https://github.com/elasticsearch/elasticsearch-groovy/issues) on GitHub which describes the feature you would
like to see, why you need it, and how it should work.

Contributing Code and Documentation Changes
-------------------------------------------

If you have a bugfix or new feature that you would like to contribute to the Elasticsearch Groovy client, please find
or open an issue about it _first_. Talk about what you would like to do. It may be that somebody is already working on
it or that there are particular issues that you should know about before implementing the change.

We enjoy working with contributors to get their code accepted. There are many approaches to fixing a problem and it is
important to find the best approach before writing too much code.

The process for contributing to any of the [Elasticsearch repositories](https://github.com/elasticsearch/) is similar.
Details for individual projects can be found below.

### Fork and Clone the Repository

You will need to fork the main Elasticsearch code or documentation repository and clone it to your local machine. See 
[GitHub help page](https://help.github.com/articles/fork-a-repo) for help.

Further instructions for specific projects are given below.

### Submitting Your Changes

Once your changes and tests are ready to submit for review:

1. Test your changes
   
   Run the test suite to make sure that nothing is broken.

2. Sign the Contributor License Agreement

   Please make sure you have signed our [Contributor License
   Agreement](http://www.elasticsearch.org/contributor-agreement/). We are not asking you to assign copyright to us,
   but to give us the right to distribute your code without restriction. We ask this of all contributors in order to
   assure our users of the origin and continuing existence of the code. You only need to sign the CLA once.

3. Rebase your changes

   Update your local repository with the most recent code from the main Elasticsearch Groovy repository, and rebase
   your branch on top of the latest master branch. We prefer your changes to be squashed into a single commit.
   
   Note: This can be done as the last step and you can rebase as the final step before a merge is accepted in order to
   make it easier to follow along during the active development and review portion of a pull request. 

4. Submit a pull request
   
   Push your local changes to your forked copy of the repository and
   [submit a pull request](https://help.github.com/articles/using-pull-requests). In the pull request, describe what
   your changes do and mention the number of the issue where discussion has taken place, eg "Closes #123".

Then sit back and wait. There will probably be discussion about the pull request and, if any changes are needed, we
would love to work with you to get your pull request merged into the Elasticsearch Groovy client.

Contributing to the Elasticsearch Groovy client
-----------------------------------------------

**Repository:** [https://github.com/elasticsearch/elasticsearch-groovy](https://github.com/elasticsearch/elasticsearch-groovy)

Make sure you have [Gradle](http://gradle.org) installed, as Elasticsearch uses it as its build system. Integration with IntelliJ and Eclipse should work out of the box thanks to Gradle.

* The build was tested with Gradle 2.1, but earlier versions will _probably_ work.

Please follow these formatting guidelines:

* Java indent is 4 spaces
* Line width is 140 characters
* The rest is left to Java coding standards
* Disable “auto-format on save” to prevent unnecessary format changes. This makes reviews much harder as it generates unnecessary formatting changes. If your IDE supports formatting only modified chunks that is fine to do.

To create a distribution from the source without running any tests, simply run:

```sh
$ cd elasticsearch-groovy/
$ gradle clean installDist
```

You will find the newly built packages under: `./build/install/elasticsearch-groovy`.

Before submitting your changes, run the test suite to make sure that nothing is broken, with:

```sh
$ gradle clean test
```

Any errors can be more easily interpreted by reading the generated error reports shown by
`./build/reports/tests/index.html`.

The Elasticsearch Groovy client reuses the Elasticsearch and Lucene test frameworks, which both expect Maven to be used
as the build tool. As a result, any test errors should display messages such as (under the Standard output tab within
the reports)

> REPRODUCE WITH  : mvn clean test -Dtests.seed=887C4F14E262EDD3
> -Dtests.class=org.elasticsearch.groovy.common.settings.ImmutableSettingsBuilderExtensionsTests -Dtests.prefix=tests 
> -Dfile.encoding=UTF-8 -Duser.timezone=America/New_York -Dtests.method="testExtensionModuleConfigured"
> -Dtests.processors=4

The above line _can_ be run by changing `mvn` to `gradle`. The rest can stay the exact same.

In order to support the test framework's functionality, all test classes must either extend `ElasticsearchTestCase` or
`ElasticsearchIntegrationTest`.

Source: [Contributing to elasticsearch](http://www.elasticsearch.org/contributing-to-elasticsearch/)
