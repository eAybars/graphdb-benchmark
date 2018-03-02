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

import static com.eaybars.benchmark.Arguments.ARGUMENTS;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class ReviewsBetween {

    //six months before 1406073600L most recent review unix time
    private long start = ARGUMENTS.extract("-query.rb.start=", Long::parseLong, 1390521600L);
    private long end = ARGUMENTS.extract("-query.rb.end=", Long::parseLong, 1393113600L);


    @Benchmark
    public List<Vertex> query(GraphSupplier graphSupplier) {
        GraphTraversalSource g = graphSupplier.traversalSource();
        List<Vertex> results = g.V().hasLabel("review")
                .has("unixReviewTime", P.between(start, end))
                .toList();
        try {
            Information.BENCHMARK_RESULT.put("ReviewsBetween-"+graphSupplier.getCount(), results.size());
        } catch (IOException e) {
        }
        return results;
    }

    @TearDown
    public void saveResults() throws IOException {
        Information.BENCHMARK_RESULT.getBuffer().forEach((name, data) -> System.out.println(name+": "+data));
        Information.BENCHMARK_RESULT.flush();
    }


}
