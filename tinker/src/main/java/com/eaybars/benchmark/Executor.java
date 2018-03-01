package com.eaybars.benchmark;

import com.eaybars.benchmark.graph.TinkerGraphSupplier;

public class Executor {

    public static void main(String[] args) throws Exception {
        GraphBenchmarkExecutor.GRAPH_SUPPLIER_CLASS = TinkerGraphSupplier.class;
        GraphBenchmarkExecutor.main(args);
    }
}
