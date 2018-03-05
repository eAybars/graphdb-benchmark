package com.eaybars.benchmark.insert.product;

import com.eaybars.benchmark.GraphSupplier;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
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
public class CategoryInsertBenchmark {

    @Benchmark
    public void benchmark(Products products, GraphSupplier graphSupplier) throws Exception {
        GraphTraversalSource g = graphSupplier.traversalSource();
        JsonObject object = products.getObject();
        Vertex product;
        try (GraphTraversal<Vertex, Vertex> traversal = g.V().hasLabel("product")
                .has("productId", object.getString("asin"));){
            product = traversal.next();
        } catch (NoSuchElementException e) {
            product = null;
        }
        if (product != null) {
            for (String category : products.getCategories()) {
                try (GraphTraversal<Vertex, Vertex> traversal = g.V().hasLabel("category")
                        .has("categoryName", category);){
                    product.addEdge("productCategory",
                            traversal.next());
                } catch (NoSuchElementException e) {
                    try (GraphTraversal<Vertex, Edge> traversal = g.addV("category")
                            .property("categoryName", category)
                            .addE("productCategory").from(product);){
                        traversal.next();
                    }
                }
            }
            //include commit time in the measurement
            graphSupplier.commit();
        }
    }
}
