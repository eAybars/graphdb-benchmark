package com.eaybars.benchmark;

import com.eaybars.benchmark.graph.JanusGraphSnSupplier;

public class Executor {

    public static void main(String[] args) throws Exception {
        GraphBenchmarkExecutor.GRAPH_SUPPLIER_CLASS = JanusGraphSnSupplier.class;
        GraphBenchmarkExecutor.main(args);
    }
}
