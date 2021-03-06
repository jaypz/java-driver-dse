/*
 *      Copyright (C) 2012-2016 DataStax Inc.
 *
 *      This software can be used solely with DataStax Enterprise. Please consult the license at
 *      http://www.datastax.com/terms/datastax-dse-driver-license-terms
 */
package com.datastax.driver.dse.graph;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import static com.datastax.driver.dse.graph.GraphAssertions.assertThat;
import static com.datastax.driver.dse.graph.TinkerGraphExtractors.vertexPropertyValue;

public class TinkerVertexAssert extends TinkerElementAssert<TinkerVertexAssert, Vertex> {

    public TinkerVertexAssert(Vertex actual) {
        super(actual, TinkerVertexAssert.class);
    }

    public TinkerVertexAssert hasProperty(String propertyName) {
        assertThat(actual.properties(propertyName)).isNotEmpty();
        return myself;
    }

    public TinkerVertexAssert hasProperty(String propertyName, Object value) {
        hasProperty(propertyName);
        assertThat(actual.properties(propertyName))
                .extracting(vertexPropertyValue())
                .contains(value);
        return myself;
    }

}
