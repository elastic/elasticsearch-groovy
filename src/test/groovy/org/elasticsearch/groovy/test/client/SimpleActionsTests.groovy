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

package org.elasticsearch.groovy.test.client

import org.elasticsearch.groovy.common.xcontent.GXContentBuilder
import org.elasticsearch.node.Node
import org.elasticsearch.node.NodeBuilder

import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo

/**
 *
 */
class SimpleActionsTests {

    def Node node

    @Before
    public void startNode() {
        NodeBuilder nodeBuilder = new NodeBuilder()
        nodeBuilder.settings {
            node {
                local = true
            }
            gateway {
                type = 'none'
            }
        }

        node = nodeBuilder.node()
    }

    @After
    public void closeNode() {
        node.close()
    }

    @Test
    void testSimpleOperations() {
        def value1 = new GXContentBuilder().buildAsString {
            something = 'test'
        }
        println value1

        def indexR = node.client.index {
            index 'test'
            type 'type1'
            id '1'
            source {
                test = 'value'
                complex {
                    value1 = 'value1'
                    value2 = 'value2'
                }
            }
        }
        assertThat indexR.response.index, equalTo('test')
        assertThat indexR.response.type, equalTo('type1')
        assertThat indexR.response.id, equalTo('1')

        def delete = node.client.delete {
            index 'test'
            type 'type1'
            id '1'
        }
        assertThat delete.response.index, equalTo('test')
        assertThat delete.response.type, equalTo('type1')
        assertThat delete.response.id, equalTo('1')

        def refresh = node.client.admin.indices.refresh {}
        assertThat refresh.response.failedShards, equalTo(0)

        def get = node.client.get {
            index 'test'
            type 'type1'
            id '1'
        }
        assertThat get.response.exists, equalTo(false)

        indexR = node.client.index {
            index 'test'
            type 'type1'
            id '1'
            source {
                test = 'value'
                complex {
                    value1 = 'value1'
                    value2 = 'value2'
                }
            }
        }
        assertThat indexR.response.index, equalTo('test')
        assertThat indexR.response.type, equalTo('type1')
        assertThat indexR.response.id, equalTo('1')

        refresh = node.client.admin.indices.refresh {}
        assertThat refresh.response.failedShards, equalTo(0)

        def theQuery = new org.elasticsearch.groovy.common.xcontent.GXContentBuilder().buildAsBytes {
            query {
                term {
                    test = 'value'
                }
            }
        }

        def count = node.client.count {
            indices 'test'
            types 'type1'
            source theQuery
        }
        assertThat count.response.failedShards, equalTo(0)
        assertThat count.response.count, equalTo(1l)

        def search = node.client.search {
            indices 'test'
            types 'type1'
            source {
                query {
                    term(test: 'value')
                }
            }
        }
        assertThat search.response.failedShards, equalTo(0)
        assertThat search.response.hits.totalHits, equalTo(1l)
        assertThat search.response.hits[0].source.test, equalTo('value')


        def updateR = node.client.update {
            index 'test'
            type 'type1'
            id '1'
            doc ( 'test', 'new value' )

        }
        assertThat updateR.response.index, equalTo('test')
        assertThat updateR.response.type, equalTo('type1')
        assertThat updateR.response.id, equalTo('1')

        get = node.client.get {
            index 'test'
            type 'type1'
            id '1'
        }
        assertThat get.response.source['test'], equalTo('new value')

        refresh = node.client.admin.indices.refresh {}
        assertThat refresh.response.failedShards, equalTo(0)

        search = node.client.search {
            indices 'test'
            types 'type1'
            source {
                query {
                    match(test: 'new value')
                }
            }
        }
        assertThat search.response.failedShards, equalTo(0)
        assertThat search.response.hits.totalHits, equalTo(1l)
        assertThat search.response.hits[0].source.test, equalTo('new value')

        def deleteByQueryQuery = new GXContentBuilder().buildAsBytes {
            query {
                term {
                    test = 'value'
                }
            }
        }

        def deleteByQuery = node.client.deleteByQuery {
            indices 'test'
            source deleteByQueryQuery
        }
        assertThat deleteByQuery.response.indices.test.failedShards, equalTo(0)

        refresh = node.client.admin.indices.refresh {}
        assertThat refresh.response.failedShards, equalTo(0)

        get = node.client.get {
            index 'test'
            type 'type1'
            id '1'
        }
        assertThat get.response.exists, equalTo(false)
    }
}
