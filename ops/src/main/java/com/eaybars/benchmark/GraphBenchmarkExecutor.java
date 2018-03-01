package com.eaybars.benchmark;

import com.eaybars.benchmark.insert.product.CategoryInsertBenchmark;
import com.eaybars.benchmark.insert.product.ProductsInsertBenchmark;
import com.eaybars.benchmark.insert.product.RelatedProductsInsertBenchmark;
import com.eaybars.benchmark.insert.review.ReviewsInsertBenchmark;
import com.eaybars.benchmark.query.simple.CategoriesAUserLikes;
import com.eaybars.benchmark.query.simple.ReviewsBetween;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.eaybars.benchmark.Arguments.ARGUMENTS;

public class GraphBenchmarkExecutor {
    public static Class<? extends Supplier<GraphTraversalSource>> GRAPH_SUPPLIER_CLASS;


    private void run() throws Exception {
        ARGUMENTS.insertFromArguments("product", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ProductsInsertBenchmark.class));
        ARGUMENTS.insertFromArguments("review", "/opt/graphdb-benchmark/reviews_Kindle_Store_5.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ReviewsInsertBenchmark.class));
        ARGUMENTS.insertFromArguments("productCategory", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(CategoryInsertBenchmark.class));
        ARGUMENTS.insertFromArguments("relatedProduct", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(RelatedProductsInsertBenchmark.class));


        ARGUMENTS.queryBenchmarkFromArguments("rb")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ReviewsBetween.class));
        ARGUMENTS.queryBenchmarkFromArguments("caul")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(CategoriesAUserLikes.class));

    }



    public static void main(String[] args) throws Exception {
        if (GRAPH_SUPPLIER_CLASS == null) {
            System.out.println("No graph supplier found:");
            System.exit(0);
        }
        System.setProperty(GraphSupplier.GRAPH_SUPPLIER_CLASS_PROPERTY, GRAPH_SUPPLIER_CLASS.getName());
        System.out.println("Running benchmarks with the following arguments: "+ Arrays.toString(args));
        ARGUMENTS.apply(args);
        new GraphBenchmarkExecutor().run();
    }


}