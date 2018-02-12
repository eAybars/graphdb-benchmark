package com.eaybars.benchmark.graph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.util.function.Supplier;

public class TinkerGraphSupplier implements Supplier<GraphTraversalSource> {
    @Override
    public GraphTraversalSource get() {
        TinkerGraph graph = TinkerGraph.open();
        return graph.traversal();
    }
}
