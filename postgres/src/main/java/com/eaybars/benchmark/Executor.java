package com.eaybars.benchmark;


import com.eaybars.benchmark.insert.Insert;
import com.eaybars.benchmark.postgres.insert.product.*;
import com.eaybars.benchmark.postgres.insert.review.ReviewsInsertBenchmark;

import java.util.function.Function;
import java.util.stream.Stream;

import static com.eaybars.benchmark.Arguments.ARGUMENTS;

public class Executor {
    private String[] args;

    private Executor(String[] args) {
        this.args = args;
    }

    private void run() throws Exception {
        ARGUMENTS.insertFromArguments("product", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ProductsInsertBenchmark.class));
        ARGUMENTS.insertFromArguments("review", "/opt/graphdb-benchmark/reviews_Kindle_Store_5.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ProductsInsertBenchmark.class));
        ARGUMENTS.insertFromArguments("productCategory", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ProductsInsertBenchmark.class));
        ARGUMENTS.insertFromArguments("relatedProduct", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ProductsInsertBenchmark.class));
    }

    public static void main(String[] args) throws Exception {
        ARGUMENTS.apply(args);
        new Executor(args).run();
    }

}
