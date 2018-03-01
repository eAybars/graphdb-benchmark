package com.eaybars.benchmark;

import com.eaybars.benchmark.graph.JanusGraphSupplier;

public class Executor {

    public static void main(String[] args) throws Exception {
        GraphBenchmarkExecutor.GRAPH_SUPPLIER_CLASS = JanusGraphSupplier.class;
        GraphBenchmarkExecutor.main(args);
    }
}
