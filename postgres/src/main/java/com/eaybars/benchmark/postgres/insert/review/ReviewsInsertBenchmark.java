package com.eaybars.benchmark.postgres.insert.review;

import com.eaybars.benchmark.insert.review.Reviews;
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
public class ReviewsInsertBenchmark {

    @Benchmark
    public void benchmark(Reviews reviews, ConnectionSupplier connectionSupplier) {
        Connection connection = connectionSupplier.getConnection();
        JsonObject object = reviews.getObject();
        //TODO add insert code

    }
}
