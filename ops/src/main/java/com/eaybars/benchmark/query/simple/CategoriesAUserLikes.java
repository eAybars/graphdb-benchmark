package com.eaybars.benchmark.query.simple;

import com.eaybars.benchmark.GraphSupplier;
import com.eaybars.benchmark.Information;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.eaybars.benchmark.Arguments.ARGUMENTS;

/**
 * A simple linear traversal
 */
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class CategoriesAUserLikes {

    private String id = ARGUMENTS.extract("-query.caul.user=", Function.identity(), "A2HEQM6A1GGSAE");


    @Benchmark
    public List<Vertex> query(GraphSupplier graphSupplier) {
        GraphTraversalSource g = graphSupplier.traversalSource();
        List<Vertex> result = g.V().hasLabel("person")
                .has("reviewerID", id)
                .out("created")//to review
                .has("overall", P.gt(3))
                .out("about")//tp product
                .out("productCategory")
                .dedup()
                .toList();
        try {
            Information.BENCHMARK_RESULT.put("CategoriesAUserLikes", result.size());
        } catch (IOException e) {
        }
        return result;
    }

    @TearDown
    public void saveResults() throws IOException {
        Information.BENCHMARK_RESULT.flush();
    }

}
