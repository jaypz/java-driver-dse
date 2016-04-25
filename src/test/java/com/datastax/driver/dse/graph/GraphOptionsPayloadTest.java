/*
 *      Copyright (C) 2012-2016 DataStax Inc.
 */
package com.datastax.driver.dse.graph;

import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GraphOptionsPayloadTest {

    @Test(groups = "unit")
    public void should_use_default_options_when_none_set() {
        GraphOptions graphOptions = new GraphOptions();

        Map<String, ByteBuffer> expectedPayload = buildPayloadFromProperties(GraphOptions.DEFAULT_GRAPH_LANGUAGE, null, GraphOptions.DEFAULT_GRAPH_SOURCE);

        Map<String, ByteBuffer> resultPayload = graphOptions.buildPayloadWithDefaults(new SimpleGraphStatement(""));

        assertThat(resultPayload).isEqualTo(expectedPayload);
    }

    @Test(groups = "unit")
    public void should_use_cluster_options_set() {
        GraphOptions graphOptions = new GraphOptions();
        graphOptions.setGraphLanguage("language1");
        graphOptions.setGraphName("name1");
        graphOptions.setGraphSource("source1");

        Map<String, ByteBuffer> expectedPayload = buildPayloadFromProperties("language1", "name1", "source1");

        Map<String, ByteBuffer> resultPayload = graphOptions.buildPayloadWithDefaults(new SimpleGraphStatement(""));

        assertThat(resultPayload).isEqualTo(expectedPayload);
    }

    @Test(groups = "unit")
    public void should_use_statement_options_over_cluster_options() {

        GraphOptions graphOptions = new GraphOptions();
        graphOptions.setGraphLanguage("language1");
        graphOptions.setGraphName("name1");
        graphOptions.setGraphSource("source1");

        SimpleGraphStatement simpleGraphStatement = new SimpleGraphStatement("");
        simpleGraphStatement.setGraphLanguage("language2");
        simpleGraphStatement.setGraphName("name2");
        simpleGraphStatement.setGraphSource("source2");

        Map<String, ByteBuffer> expectedPayload = buildPayloadFromStatement(simpleGraphStatement);

        Map<String, ByteBuffer> resultPayload = graphOptions.buildPayloadWithDefaults(simpleGraphStatement);

        assertThat(resultPayload).isEqualTo(expectedPayload);
    }

    @Test(groups = "unit", expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "graphLanguage cannot be null")
    public void should_not_allow_null_on_graph_language_on_cluster() {
        GraphOptions graphOptions = new GraphOptions();

        graphOptions.setGraphLanguage(null);
    }

    @Test(groups = "unit", expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "graphSource cannot be null")
    public void should_not_allow_null_on_graph_source_on_cluster() {
        GraphOptions graphOptions = new GraphOptions();

        graphOptions.setGraphSource(null);
    }

    @Test(groups = "unit")
    public void should_allow_null_graph_name_on_cluster() {
        GraphOptions graphOptions = new GraphOptions();
        graphOptions.setGraphLanguage("language1");
        graphOptions.setGraphSource("source1");
        graphOptions.setGraphName(null);

        Map<String, ByteBuffer> expectedPayload = buildPayloadFromProperties("language1", null, "source1");

        Map<String, ByteBuffer> resultPayload = graphOptions.buildPayloadWithDefaults(new SimpleGraphStatement(""));

        assertThat(resultPayload).isEqualTo(expectedPayload);

    }

    @Test(groups = "unit")
    public void should_force_no_graph_name_if_statement_is_a_system_query() {
        GraphOptions graphOptions = new GraphOptions();
        graphOptions.setGraphLanguage("language1");
        graphOptions.setGraphName("name1");
        graphOptions.setGraphSource("source1");

        SimpleGraphStatement simpleGraphStatement = new SimpleGraphStatement("");
        simpleGraphStatement.setSystemQuery();

        Map<String, ByteBuffer> expectedPayload = buildPayloadFromProperties("language1", null, "source1");

        Map<String, ByteBuffer> resultPayload = graphOptions.buildPayloadWithDefaults(simpleGraphStatement);

        assertThat(resultPayload).isEqualTo(expectedPayload);
    }

    private Map<String, ByteBuffer> buildPayloadFromStatement(GraphStatement graphStatement) {
        return ImmutableMap.of(
                GraphOptions.GRAPH_LANGUAGE_KEY, PayloadHelper.asBytes(graphStatement.getGraphLanguage()),
                GraphOptions.GRAPH_NAME_KEY, PayloadHelper.asBytes(graphStatement.getGraphName()),
                GraphOptions.GRAPH_SOURCE_KEY, PayloadHelper.asBytes(graphStatement.getGraphSource())
        );
    }

    private Map<String, ByteBuffer> buildPayloadFromProperties(String graphLanguage, String graphName, String graphSource) {
        ImmutableMap.Builder<String, ByteBuffer> builder = ImmutableMap.builder();
        if (graphLanguage != null) {
            builder.put(GraphOptions.GRAPH_LANGUAGE_KEY, PayloadHelper.asBytes(graphLanguage));
        }
        if (graphName != null) {
            builder.put(GraphOptions.GRAPH_NAME_KEY, PayloadHelper.asBytes(graphName));
        }
        if (graphSource != null) {
            builder.put(GraphOptions.GRAPH_SOURCE_KEY, PayloadHelper.asBytes(graphSource));
        }
        return builder.build();
    }

}