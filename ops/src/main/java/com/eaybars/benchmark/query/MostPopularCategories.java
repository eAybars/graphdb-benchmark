package com.eaybars.benchmark.query;

import com.eaybars.benchmark.ExecutorDelegation;
import com.eaybars.benchmark.GraphSupplier;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class MostPopularCategories {

    @Benchmark
    public List<Vertex> query(GraphSupplier graphSupplier) {
        GraphTraversalSource g = graphSupplier.traversalSource();

        List<Vertex> list = g.V().hasLabel("category")
                .order().by(
                        in("productCategory")
                                .in("about").has("overall", P.gte(4.0)).values("overall").sum(), Order.decr)                .limit(5)
                .toStream().collect(Collectors.toList());
        System.out.println(list);
        return list;
    }

    public static void run(int times) throws RunnerException {
        Options build = new OptionsBuilder()
                .include(MostPopularCategories.class.getName())
                .warmupIterations(0)
                .measurementIterations(times)
                .timeout(TimeValue.hours(4))
                .forks(ExecutorDelegation.forks())
                .build();

        new Runner(build).run();
    }
}
