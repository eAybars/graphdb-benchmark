package com.eaybars.benchmark;

import com.eaybars.benchmark.graph.JanusGraphSupplier;

public class Executor {

    public static void main(String[] args) throws Exception {
        ExecutorDelegation.GRAPH_SUPPLIER_CLASS = JanusGraphSupplier.class;
        ExecutorDelegation.main(args);
    }
}
