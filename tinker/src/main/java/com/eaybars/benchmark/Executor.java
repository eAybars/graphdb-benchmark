package com.eaybars.benchmark;

import com.eaybars.benchmark.ops.Ops;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.util.Arrays;
import java.util.HashSet;

public class Executor {

    public static void main(String[] args) {
        TinkerGraph graph = TinkerGraph.open();
        new Ops().accept(new HashSet<>(Arrays.asList(args)), graph.traversal());
        graph.close();
    }
}
