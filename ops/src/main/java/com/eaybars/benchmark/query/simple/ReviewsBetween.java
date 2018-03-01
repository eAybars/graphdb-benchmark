package com.eaybars.benchmark.query.simple;

import com.eaybars.benchmark.GraphSupplier;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.openjdk.jmh.annotations.*;

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
    public Number query(GraphSupplier graphSupplier) {
        GraphTraversalSource g = graphSupplier.traversalSource();
        return g.V().hasLabel("review")
                .has("unixReviewTime", P.between(start, end))
                .count().next();
    }


}
