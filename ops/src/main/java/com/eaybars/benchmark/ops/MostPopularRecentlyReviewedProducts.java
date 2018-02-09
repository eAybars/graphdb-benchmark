package com.eaybars.benchmark.ops;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

public class MostPopularRecentlyReviewedProducts implements BiConsumer<Set<String>, GraphTraversalSource> {
    @Override
    public void accept(Set<String> args, GraphTraversalSource g) {
        if (!args.contains("-q") && args.stream().noneMatch(s -> s.matches("-q=.*pr.*"))) {
            return;
        }

        long startTime = System.currentTimeMillis();
        Number maxReviewTime = g.V().hasLabel("review").properties("unixReviewTime").value().max().next();
        long endTime = System.currentTimeMillis();

        long recently = maxReviewTime.longValue() - 15_552_000_000L; //6 months earlier

        System.out.println("Most recent review time: " + maxReviewTime);
        System.out.println("Time to query most recent review time: " + (endTime - startTime));

        startTime = System.currentTimeMillis();


        List<Vertex> list = g.V().hasLabel("review")
                .has("unixReviewTime", P.gt(recently))
                .out("about")
                .dedup()
                .order().by(inE("about").count(), Order.decr)
                .limit(5)
                .toStream().collect(Collectors.toList());

        endTime = System.currentTimeMillis();

        System.out.println("Time to query recently reviewed 5 most popular products: " + (endTime - startTime));

        System.out.println("ProductId\tRank\tTime");
        for (Vertex vertex : list) {
            startTime = System.currentTimeMillis();
            Long rating = g.V(vertex.id()).inE("about").count().next();
            endTime = System.currentTimeMillis();
            System.out.println(vertex.properties("productId").next().value() + "\t" + rating + "\t\t" + (endTime - startTime));
        }

    }
}
