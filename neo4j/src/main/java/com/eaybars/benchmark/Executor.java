package com.eaybars.benchmark;


import com.eaybars.benchmark.graph.Neo4jDbSupplier;

public class Executor {

    public static void main(String[] args) throws Exception {
        ExecutorDelegation.GRAPH_SUPPLIER_CLASS = Neo4jDbSupplier.class;
        ExecutorDelegation.main(args);
    }
}
