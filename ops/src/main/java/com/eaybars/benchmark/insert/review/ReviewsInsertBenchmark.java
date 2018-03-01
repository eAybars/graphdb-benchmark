package com.eaybars.benchmark.insert.review;

import com.eaybars.benchmark.GraphSupplier;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import javax.json.JsonObject;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class ReviewsInsertBenchmark {

    @Benchmark
    public void benchmark(GraphSupplier graph, Reviews is) {
        GraphTraversalSource g = graph.traversalSource();
        JsonObject object = is.getObject();

        Vertex review = g.getGraph().addVertex("review");
        review.property("reviewText", object.getString("reviewText"));
        review.property("overall", object.getJsonNumber("overall").doubleValue());
        review.property("summary", object.getString("summary"));
        review.property("unixReviewTime", object.getJsonNumber("unixReviewTime").longValue());

        Vertex user;
        try {
            user = g.V().hasLabel("person").has("reviewerID", object.getString("reviewerID")).next();
        } catch (NoSuchElementException e) {
            user = g.addV("person")
                    .property("reviewerID", object.getString("reviewerID"))
                    .property("reviewerName", object.getString("reviewerName", "anonymous"))
                    .next();
        }

        Vertex product;
        try {
            product = g.V().hasLabel("product").has("productId", object.getString("asin")).next();
        } catch (NoSuchElementException e) {
            product = g.addV("product")
                    .property("productId", object.getString("asin"))
                    .next();
        }


        user.addEdge("created", review, "unixReviewTime", object.getJsonNumber("unixReviewTime").longValue());
        review.addEdge("about", product);

    }

}
