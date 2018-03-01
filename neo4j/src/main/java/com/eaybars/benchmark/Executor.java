package com.eaybars.benchmark;


import com.eaybars.benchmark.graph.Neo4jDbSupplier;

public class Executor {

    public static void main(String[] args) throws Exception {
        GraphBenchmarkExecutor.GRAPH_SUPPLIER_CLASS = Neo4jDbSupplier.class;
        GraphBenchmarkExecutor.main(args);
    }
}
