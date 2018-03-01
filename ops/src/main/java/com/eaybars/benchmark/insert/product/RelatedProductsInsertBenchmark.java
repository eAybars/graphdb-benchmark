package com.eaybars.benchmark.insert.product;

import com.eaybars.benchmark.GraphSupplier;
import org.apache.tinkerpop.gremlin.process.traversal.P;
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
public class RelatedProductsInsertBenchmark {

    @Benchmark
    public void benchmark(Products products, GraphSupplier graphSupplier) {
        GraphTraversalSource g = graphSupplier.traversalSource();
        JsonObject object = products.getObject();

        Vertex product;
        try {
            product = g.V().hasLabel("product").has("productId", object.getString("asin")).next();
        } catch (NoSuchElementException e) {
            product = null;
        }
        if (product != null) {
            if (!products.getAlsoViewed().isEmpty()) {
                g.V().hasLabel("product").has("productId", P.within(products.getAlsoViewed()))
                        .addE("also_viewed").from(product).count().next();
            }
            if (!products.getBuyAfterViewing().isEmpty()) {
                g.V().hasLabel("product").has("productId", P.within(products.getBuyAfterViewing()))
                        .addE("buy_after_viewing").from(product).count().next();
            }
        }

    }
}
