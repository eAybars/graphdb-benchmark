package com.eaybars.benchmark;


import com.eaybars.benchmark.insert.product.CategoryInsertDocumentBenchmark;
import com.eaybars.benchmark.insert.product.ProductsInsertDocumentBenchmark;
import com.eaybars.benchmark.insert.product.RelatedProductsInsertDocumentBenchmark;
import com.eaybars.benchmark.insert.review.ReviewsInsertDocumentBenchmark;

import static com.eaybars.benchmark.Arguments.ARGUMENTS;

public class Executor {

    private void run() throws Exception {
        ARGUMENTS.insertFromArguments("product", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ProductsInsertDocumentBenchmark.class));
        ARGUMENTS.insertFromArguments("review", "/opt/graphdb-benchmark/reviews_Kindle_Store_5.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ReviewsInsertDocumentBenchmark.class));
        ARGUMENTS.insertFromArguments("productCategory", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(CategoryInsertDocumentBenchmark.class));
        ARGUMENTS.insertFromArguments("relatedProduct", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(RelatedProductsInsertDocumentBenchmark.class));
    }

    public static void main(String[] args) throws Exception {
        ARGUMENTS.apply(args);
        new Executor().run();
    }

}
