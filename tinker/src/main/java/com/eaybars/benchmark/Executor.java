package com.eaybars.benchmark;

import com.eaybars.benchmark.graph.TinkerGraphSupplier;

public class Executor {

    public static void main(String[] args) throws Exception {
//        Insert.using(TinkerGraphSupplier.class)
//                .reviewsFile("/Users/ertunc/github/graphdb-benchmark/ops/reviews_Kindle_Store_5.json.gz")
//                .insertCount(5000)
//                .measurementBatchSize(100)
//                .run();
        ExecutorDelegation.GRAPH_SUPPLIER_CLASS = TinkerGraphSupplier.class;
        ExecutorDelegation.main(args);
    }
}
