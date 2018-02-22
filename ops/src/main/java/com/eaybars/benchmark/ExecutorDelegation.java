package com.eaybars.benchmark;

import com.eaybars.benchmark.insert.Insert;
import com.eaybars.benchmark.insert.product.CategoryInsertBenchmark;
import com.eaybars.benchmark.insert.product.ProductsInsertBenchmark;
import com.eaybars.benchmark.insert.product.RelatedProductsInsertBenchmark;
import com.eaybars.benchmark.insert.review.ReviewsInsertBenchmark;
import com.eaybars.benchmark.query.MostPopularCategories;
import com.eaybars.benchmark.query.MostRecentReviewTimeByLimit;
import com.eaybars.benchmark.query.MostRecentReviewTimeByMax;
import com.eaybars.benchmark.query.RecentPopularProducts;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ExecutorDelegation {
    public static Class<? extends Supplier<GraphTraversalSource>> GRAPH_SUPPLIER_CLASS;

    private static final Object MONITOR = new Object();
    private static volatile ExecutorDelegation instance;
    private String[] args;

    public ExecutorDelegation(String[] args) {
        this.args = args;
    }

    public static ExecutorDelegation getInstance() {
        return instance;
    }

    private void run() throws Exception {
        checkAndRunInsert(ProductsInsertBenchmark.class, "-insert.product.", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz");
        checkAndRunInsert(ReviewsInsertBenchmark.class, "-insert.review.", "/opt/graphdb-benchmark/reviews_Kindle_Store_5.json.gz");
        checkAndRunInsert(CategoryInsertBenchmark.class, "-insert.productCategory.", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz");
        checkAndRunInsert(RelatedProductsInsertBenchmark.class, "-insert.relatedProduct.", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz");

        int times = extract("-query.mrrtbm=", Integer::parseInt, 0);
        if (times > 0) {
            MostRecentReviewTimeByMax.run(times);
        }

        times = extract("-query.mrrtbl=", Integer::parseInt, 0);
        if (times > 0) {
            MostRecentReviewTimeByLimit.run(times);
        }

        times = extract("-query.rpp=", Integer::parseInt, 0);
        if (times > 0) {
            RecentPopularProducts.run(times);
        }

        times = extract("-query.mpc=", Integer::parseInt, 0);
        if (times > 0) {
            MostPopularCategories.run(times);
        }
    }

    private void checkAndRunInsert(Class<?> type, String prefix, String defaultFile) throws Exception {
        if (extract(prefix, s -> true, false)) {
            Insert.fromFile(extract(withPrefix(prefix, "file="), Function.identity(), defaultFile))
                    .insertCount(extract(withPrefix(prefix, "count="), Integer::parseInt, -1))
                    .startingFrom(extract(withPrefix(prefix, "start="), Integer::parseInt, 0))
                    .measurementBatchSize(extract(withPrefix(prefix, "measurementBatch="), Integer::parseInt, 1000))
                    .commitInterval(extract(withPrefix(prefix, "commit="), Integer::parseInt, 20))
                    .run(type);
        }
    }

    private String withPrefix(String prefix, String param) {
        return prefix+param;
    }

    public static void main(String[] args) throws Exception {
        if (GRAPH_SUPPLIER_CLASS == null) {
            System.out.println("No graph supplier found:");
            System.exit(0);
        }
        boolean execute = false;
        if (instance == null) {
            synchronized (MONITOR) {
                if (instance == null) {
                    instance = new ExecutorDelegation(args);
                    execute = true;
                }
            }
        }
        if (execute) {
            System.setProperty(GraphSupplier.GRAPH_SUPPLIER_CLASS_PROPERTY, GRAPH_SUPPLIER_CLASS.getName());
            System.out.println("Running benchmarks with the following arguments: "+ Arrays.toString(args));
            instance.run();
        }
    }

    public <T> T extract(String param, Function<String, T> mapper, T defaultValue) {
        return Stream.of(args)
                .filter(arg -> arg.startsWith(param))
                .map(arg -> arg.substring(param.length()))
                .map(mapper)
                .findFirst()
                .orElse(defaultValue);
    }

    public static int forks() {
        int fork;
        try {
            fork = Integer.parseInt(Optional.ofNullable(System.getenv("JMH_FORK")).orElse("0"));
        } catch (NumberFormatException e) {
            fork = 0;
        }
        return fork;
    }
}
