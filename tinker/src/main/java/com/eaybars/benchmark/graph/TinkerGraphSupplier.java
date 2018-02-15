package com.eaybars.benchmark.graph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.util.function.Supplier;

public class TinkerGraphSupplier implements Supplier<GraphTraversalSource> {
    private static final TinkerGraph GRAPH = TinkerGraph.open();
    @Override
    public GraphTraversalSource get() {
        return GRAPH.traversal();
    }
}
