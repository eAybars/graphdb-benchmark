package com.eaybars.benchmark.query;

import com.eaybars.benchmark.ExecutorDelegation;
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
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class MostRecentReviewTimeByMax {

    @Benchmark
    public Number query(GraphSupplier graphSupplier) {
        GraphTraversalSource g = graphSupplier.traversalSource();
        return g.V().hasLabel("review").values("unixReviewTime").max().next();
    }

    public static void run(int times) throws RunnerException {
        Options build = new OptionsBuilder()
                .include(MostRecentReviewTimeByMax.class.getName())
                .warmupIterations(0)
                .measurementIterations(times)
                .timeout(TimeValue.hours(4))
                .forks(ExecutorDelegation.forks())
                .build();

        new Runner(build).run();
    }
}
