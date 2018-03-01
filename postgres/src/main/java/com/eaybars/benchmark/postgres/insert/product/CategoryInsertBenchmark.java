package com.eaybars.benchmark.postgres.insert.product;

import com.eaybars.benchmark.insert.product.Products;
import com.eaybars.benchmark.postgres.insert.ConnectionSupplier;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import javax.json.JsonObject;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class CategoryInsertBenchmark {

    @Benchmark
    public void benchmark(Products products, ConnectionSupplier connectionSupplier) {
        Connection connection = connectionSupplier.getConnection();
        JsonObject object = products.getObject();
        //TODO add insert code

    }
}
