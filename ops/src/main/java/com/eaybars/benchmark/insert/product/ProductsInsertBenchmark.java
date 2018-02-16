package com.eaybars.benchmark.insert.product;

import com.eaybars.benchmark.GraphSupplier;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class ProductsInsertBenchmark {

    @Benchmark
    public void benchmark(Products products, GraphSupplier graphSupplier) {
        GraphTraversalSource g = graphSupplier.traversalSource();
        JsonObject object = products.getObject();

        g.addV("product")
                .property("productId", object.getString("asin"))
                .property("description", object.getString("description", ""))
                .property("price", Optional.ofNullable(object.getJsonNumber("price")).map(JsonNumber::doubleValue).orElse(0.0))
                .property("imUrl", object.getString("imUrl", ""))
                .next();

        if (products.getCommitInterval() == 1 || products.getCount() % products.getCommitInterval() == 0) {
            try {
                g.getGraph().tx().commit();
            } catch (Exception e) {
            }
        }
    }
}
