package com.eaybars.benchmark.ops;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.util.Set;
import java.util.function.BiConsumer;

public class Ops implements BiConsumer<Set<String>, GraphTraversalSource> {

    @Override
    public void accept(Set<String> args, GraphTraversalSource g) {
        new Insert()
                .andThen(new MostPopularRecentlyReviewedProducts())
                .accept(args, g);
    }
}
