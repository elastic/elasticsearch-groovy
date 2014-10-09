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

//@Grapes([
//    @Grab(group = 'org.elasticsearch', module = 'elasticsearch-groovy', version = '0.7.0-SNAPSHOT'),
//    @Grab(group = 'org.slf4j', module = 'slf4j-simple', version = '1.5.8')
///*    @Grab(group = 'org.slf4j', module = 'slf4j-log4j12', version = '1.5.8')*/
//])

def startNode() {
    def nodeBuilder = new NodeBuilder()
    nodeBuilder.settings {
        node {
            client = true
        }
    }
    nodeBuilder.node()
}


def node = startNode()

println "settings $node.settings.asMap"

println "Node started"

future = node.client.index {
    index "twitter"
    lang "tweet"
    id "1"
    source {
        user = "kimchy"
        message = "this is a tweet"
    }
}

println "Indexed $future.response.index/$future.response.type/$future.response.id"

node.close()
