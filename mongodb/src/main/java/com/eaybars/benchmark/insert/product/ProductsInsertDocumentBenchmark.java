package com.eaybars.benchmark.insert.product;

import com.eaybars.benchmark.insert.ConnectionSupplier;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import javax.json.JsonObject;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class ProductsInsertDocumentBenchmark {

    @Benchmark
    public void benchmark(Products products, ConnectionSupplier connectionSupplier) {
        Connection connection = connectionSupplier.getConnection();
        JsonObject object = products.getObject();
        //TODO

        connectionSupplier.commit();

    }
}
