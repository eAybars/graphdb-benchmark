package com.eaybars.benchmark;

import com.eaybars.benchmark.graph.JanusGraphSupplier;
import com.eaybars.benchmark.ops.Ops;
import org.apache.tinkerpop.gremlin.structure.Graph;

import java.util.Arrays;
import java.util.HashSet;

public class Executor {

    public static void main(String[] args) throws Exception {
        Graph graph = new JanusGraphSupplier().get();
        new Ops().accept(new HashSet<>(Arrays.asList(args)), graph.traversal());
        graph.close();
    }
}
