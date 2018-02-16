package com.eaybars.benchmark.query;

import com.eaybars.benchmark.GraphSupplier;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.inE;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class RecentPopularProducts {

    @Benchmark
    public List<Vertex> query(GraphSupplier graphSupplier) {
        GraphTraversalSource g = graphSupplier.traversalSource();

        long mostRecentReviewTime = Long.parseLong(System.getProperty(
                MostRecentReviewTime.MOST_RECENT_REVIEW_TIME_RESULT,
                String.valueOf(System.currentTimeMillis())));

        long recently = mostRecentReviewTime - 15_552_000_000L; //6 months earlier

        List<Vertex>  res = g.V().hasLabel("review")
                .has("unixReviewTime", P.gt(recently))
                .out("about")
                .dedup()
                .order().by(inE("about").count(), Order.decr)
                .limit(5)
                .toStream().collect(Collectors.toList());

        return res;
    }

    public static void run(int times) throws RunnerException {
        Options build = new OptionsBuilder()
                .include(RecentPopularProducts.class.getName())
                .warmupIterations(0)
                .measurementIterations(times)
                .forks(0)
                .build();

        new Runner(build).run();
    }

}
