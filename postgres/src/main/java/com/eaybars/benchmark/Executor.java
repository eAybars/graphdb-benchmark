package com.eaybars.benchmark;


import com.eaybars.benchmark.insert.product.CategoryInsertSQLBenchmark;
import com.eaybars.benchmark.insert.product.ProductsInsertSQLBenchmark;
import com.eaybars.benchmark.insert.product.RelatedProductsInsertSQLBenchmark;
import com.eaybars.benchmark.insert.review.ReviewsInsertSQLBenchmark;

import static com.eaybars.benchmark.Arguments.ARGUMENTS;

public class Executor {

    private void run() throws Exception {
        ARGUMENTS.insertFromArguments("product", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ProductsInsertSQLBenchmark.class));
        ARGUMENTS.insertFromArguments("review", "/opt/graphdb-benchmark/reviews_Kindle_Store_5.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ReviewsInsertSQLBenchmark.class));
        ARGUMENTS.insertFromArguments("productCategory", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(CategoryInsertSQLBenchmark.class));
        ARGUMENTS.insertFromArguments("relatedProduct", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(RelatedProductsInsertSQLBenchmark.class));
    }

    public static void main(String[] args) throws Exception {
        ARGUMENTS.apply(args);
        new Executor().run();
    }

}
