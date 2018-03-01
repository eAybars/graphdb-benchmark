package com.eaybars.benchmark;

import com.eaybars.benchmark.graph.OrientDbSupplier;

public class Executor {

    public static void main(String[] args) throws Exception {
        GraphBenchmarkExecutor.GRAPH_SUPPLIER_CLASS = OrientDbSupplier.class;
        GraphBenchmarkExecutor.main(args);
    }
}
