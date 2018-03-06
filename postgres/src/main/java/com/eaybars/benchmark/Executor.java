package com.eaybars.benchmark;


import com.eaybars.benchmark.postgres.insert.product.CategoryInsertBenchmark;
import com.eaybars.benchmark.postgres.insert.product.ProductsInsertBenchmark;
import com.eaybars.benchmark.postgres.insert.product.RelatedProductsInsertBenchmark;
import com.eaybars.benchmark.postgres.insert.review.ReviewsInsertBenchmark;

import static com.eaybars.benchmark.Arguments.ARGUMENTS;

public class Executor {

    private void run() throws Exception {
        ARGUMENTS.insertFromArguments("product", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ProductsInsertBenchmark.class));
        ARGUMENTS.insertFromArguments("review", "/opt/graphdb-benchmark/reviews_Kindle_Store_5.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ReviewsInsertBenchmark.class));
        ARGUMENTS.insertFromArguments("productCategory", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(CategoryInsertBenchmark.class));
        ARGUMENTS.insertFromArguments("relatedProduct", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(RelatedProductsInsertBenchmark.class));
    }

    public static void main(String[] args) throws Exception {
        ARGUMENTS.apply(args);
        new Executor().run();
    }

}
