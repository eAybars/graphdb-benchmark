package com.eaybars.benchmark;

import com.eaybars.benchmark.graph.OrientDbSupplier;
import com.eaybars.benchmark.ops.Ops;

import java.util.Arrays;
import java.util.HashSet;

public class Executor {

    public static void main(String[] args) {
        new Ops().accept(new HashSet<>(Arrays.asList(args)), new OrientDbSupplier().get().traversal());
    }
}
