package com.eaybars.benchmark.query.simple;

import com.eaybars.benchmark.GraphSupplier;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.eaybars.benchmark.Arguments.ARGUMENTS;
import static org.apache.tinkerpop.gremlin.process.traversal.P.eq;
import static org.apache.tinkerpop.gremlin.process.traversal.P.neq;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.out;

/**
 * A simple linear traversal
 */
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class PeopleAUserIsAlike {

    private String id = ARGUMENTS.extract("-query.caul.user=", Function.identity(), "A2HEQM6A1GGSAE");


    @Benchmark
    public List<Vertex> query(GraphSupplier graphSupplier) {
        GraphTraversalSource g = graphSupplier.traversalSource();
        return g.V().hasLabel("person")
                .has("reviewerID", id)
                .as("user")
                .out("created")//to review
                .out("about")//tp product
                .as("products")
                .in("about")//reviews to the products the user bought
                .in("created")
                .where(neq("user"))
                .dedup()
                .order().by(out("created").out("about").where(eq("products")).count(), Order.decr)
                .toList();
    }


}
