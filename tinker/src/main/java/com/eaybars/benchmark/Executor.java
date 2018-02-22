package com.eaybars.benchmark;

import com.eaybars.benchmark.graph.TinkerGraphSupplier;

public class Executor {

    public static void main(String[] args) throws Exception {
        ExecutorDelegation.GRAPH_SUPPLIER_CLASS = TinkerGraphSupplier.class;
        ExecutorDelegation.main(args);
    }
}
