package com.eaybars.benchmark.insert.product;

import com.eaybars.benchmark.GraphSupplier;
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
    public void benchmark(Products products, GraphSupplier graphSupplier) {
        GraphTraversalSource g = graphSupplier.traversalSource();
        JsonObject object = products.getObject();
        Vertex product = g.V().hasLabel("product").has("productId", object.getString("asin")).next();
        if (product != null) {
            if (!products.getCategories().isEmpty()) {
                for (String category : products.getCategories()) {
                    try {
                        Edge edge = product.addEdge("productCategory",
                                g.V("category").has("categoryName", category).next());
                        if (edge != null) {
                            System.out.println(edge);
                        }
                    } catch (NoSuchElementException e) {
                        Edge next = g.addV("category")
                                .property("categoryName", category)
                                .addE("productCategory").from(product).next();
                        if (next != null) {
                            System.out.println(next);
                        }
                    }
                }
            }
        }


        if (products.getCommitInterval() == 1 || products.getCount() % products.getCommitInterval() == 0) {
            try {
                g.getGraph().tx().commit();
            } catch (Exception e) {
            }
        }
    }
}