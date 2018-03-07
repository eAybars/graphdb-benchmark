package com.eaybars.benchmark.insert.review;

import com.eaybars.benchmark.GraphSupplier;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
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
public class ReviewInsertBenchmark {

    @Benchmark
    public void benchmark(GraphSupplier graphSupplier, Reviews is) throws Exception {
        GraphTraversalSource g = graphSupplier.traversalSource();
        JsonObject object = is.getObject();

        Vertex review = g.getGraph().addVertex("review");
        review.property("reviewText", object.getString("reviewText"));
        review.property("overall", object.getJsonNumber("overall").doubleValue());
        review.property("summary", object.getString("summary"));
        review.property("unixReviewTime", object.getJsonNumber("unixReviewTime").longValue());

        Vertex user;
        try (GraphTraversal<Vertex, Vertex> traversal = g.V().hasLabel("person")
                .has("reviewerID", object.getString("reviewerID"));) {
            user = traversal.next();
        } catch (NoSuchElementException e) {
            user = g.getGraph().addVertex("person");
            user.property("reviewerID", object.getString("reviewerID"));
            user.property("reviewerName", object.getString("reviewerName", "anonymous"));

//            try (GraphTraversal<Vertex, Vertex> traversal = g.addV("person")
//                    .property("reviewerID", object.getString("reviewerID"))
//                    .property("reviewerName", object.getString("reviewerName", "anonymous"));) {
//                user = traversal.next();
//            }
        }

        Vertex product;
        try (GraphTraversal<Vertex, Vertex> traversal = g.V().hasLabel("product")
                .has("productId", object.getString("asin"));) {
            product = traversal.next();
        } catch (NoSuchElementException e) {
            product = g.getGraph().addVertex("product");
            product.property("productId", object.getString("asin"));
//            try (GraphTraversal<Vertex, Vertex> traversal = g.addV("product")
//                    .property("productId", object.getString("asin"));) {
//                product = traversal.next();
//            }
        }


        user.addEdge("created", review, "unixReviewTime", object.getJsonNumber("unixReviewTime").longValue());
        review.addEdge("about", product);

        //include commit time in the measurement
        graphSupplier.commit();
    }

}
