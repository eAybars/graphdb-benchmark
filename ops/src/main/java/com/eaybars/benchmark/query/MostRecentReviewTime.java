package com.eaybars.benchmark.query;

import com.eaybars.benchmark.GraphSupplier;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class MostRecentReviewTime {
    public static final String MOST_RECENT_REVIEW_TIME_RESULT = "MostRecentReviewTime.result";

    @Benchmark
    public void query(GraphSupplier graphSupplier) {
        GraphTraversalSource g = graphSupplier.traversalSource();
        Number maxReviewTime = g.V().hasLabel("review").properties("unixReviewTime").value().max().next();
        System.setProperty(MOST_RECENT_REVIEW_TIME_RESULT, String.valueOf(maxReviewTime.longValue()));
    }

    public static void run(int times) throws RunnerException {
        Options build = new OptionsBuilder()
                .include(MostRecentReviewTime.class.getName())
                .warmupIterations(0)
                .measurementIterations(times)
                .forks(0)
                .build();

        new Runner(build).run();
    }
}
