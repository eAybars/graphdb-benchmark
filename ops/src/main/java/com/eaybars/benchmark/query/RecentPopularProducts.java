package com.eaybars.benchmark.query;

import com.eaybars.benchmark.GraphSupplier;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.eaybars.benchmark.Arguments.ARGUMENTS;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.inE;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class RecentPopularProducts {

    //six months before 1406073600L most recent review unix time
    private long reviewTime = ARGUMENTS.extract("-query.rpp.reviewTime=", Long::parseLong, 1390521600L);

    @Benchmark
    public List<Vertex> query(GraphSupplier graphSupplier) {
        GraphTraversalSource g = graphSupplier.traversalSource();

        List<Vertex>  res = g.V().hasLabel("review")
                .has("unixReviewTime", P.gt(reviewTime))
                .out("about")
                .dedup()
                .order().by(inE("about").count(), Order.decr)
                .limit(5)
                .toStream().collect(Collectors.toList());

        return res;
    }


}
