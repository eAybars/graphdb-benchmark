package com.eaybars.benchmark;

import com.eaybars.benchmark.insert.product.CategoryInsertBenchmark;
import com.eaybars.benchmark.insert.product.ProductInsertBenchmark;
import com.eaybars.benchmark.insert.product.RelatedProductsInsertBenchmark;
import com.eaybars.benchmark.insert.review.ReviewInsertBenchmark;
import com.eaybars.benchmark.query.CategoriesAUserLikes;
import com.eaybars.benchmark.query.PeopleAUserIsAlike;
import com.eaybars.benchmark.query.ReviewsBetween;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.util.function.Supplier;

import static com.eaybars.benchmark.Arguments.ARGUMENTS;

public class GraphBenchmarkExecutor {
    public static Class<? extends Supplier<GraphTraversalSource>> GRAPH_SUPPLIER_CLASS;


    private void run() throws Exception {
        ARGUMENTS.insertFromArguments("product", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ProductInsertBenchmark.class));
        ARGUMENTS.insertFromArguments("review", "/opt/graphdb-benchmark/reviews_Kindle_Store_5.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ReviewInsertBenchmark.class));
        ARGUMENTS.insertFromArguments("productCategory", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(CategoryInsertBenchmark.class));
        ARGUMENTS.insertFromArguments("relatedProduct", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(RelatedProductsInsertBenchmark.class));


        ARGUMENTS.queryBenchmarkFromArguments("rb")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(ReviewsBetween.class));
        ARGUMENTS.queryBenchmarkFromArguments("caul")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(CategoriesAUserLikes.class));
        ARGUMENTS.queryBenchmarkFromArguments("pauia")
                .ifPresent(BenchmarkRunnerConsumer.forBenchmark(PeopleAUserIsAlike.class));

        System.out.println();
        System.out.println("Benchmark results: ");
        Information.BENCHMARK_RESULT.objects().forEachRemaining(e -> System.out.println(e.getKey()+": "+e.getValue()));
    }



    public static void main(String[] args) throws Exception {
        if (GRAPH_SUPPLIER_CLASS == null) {
            System.out.println("No graph supplier found:");
            System.exit(0);
        }
        System.setProperty(GraphSupplier.GRAPH_SUPPLIER_CLASS_PROPERTY, GRAPH_SUPPLIER_CLASS.getName());
        ARGUMENTS.apply(args);
        new GraphBenchmarkExecutor().run();
    }


}
