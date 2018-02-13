package com.eaybars.benchmark;

import com.eaybars.benchmark.insert.Insert;
import com.eaybars.benchmark.insert.review.ReviewsInsertBenchmark;
import com.eaybars.benchmark.query.MostRecentReviewTime;
import com.eaybars.benchmark.query.RecentPopularProducts;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import javax.naming.spi.NamingManager;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ExecutorDelegation {
    public static Class<? extends Supplier<GraphTraversalSource>> GRAPH_SUPPLIER_CLASS;

    private String[] args;

    public ExecutorDelegation(String[] args) {
        this.args = args;
    }

    public void run() throws Exception {
        if (extract("-insert.review", s -> true, false)) {
            Insert.fromFile(extract("-insert.review.file=", Function.identity(), "/opt/graphdb-benchmark/reviews_Kindle_Store_5.json.gz"))
                    .insertCount(extract("-insert.review.count=", Integer::parseInt, -1))
                    .measurementBatchSize(extract("-insert.review.measurementBatch=", Integer::parseInt, 1000))
                    .commitInterval(extract("-insert.review.commit=", Integer::parseInt, 20))
                    .run(ReviewsInsertBenchmark.class);
        }

        int times = extract("-q.mrrt=", Integer::parseInt, 0);
        if (times > 0) {
            MostRecentReviewTime.run(times);
        }

        times = extract("-q.rpp=", Integer::parseInt, 0);
        if (times > 0) {
            RecentPopularProducts.run(times);
        }
    }

    public static void main(String[] args) throws Exception {
        if (GRAPH_SUPPLIER_CLASS == null) {
            System.out.println("No graph supplier found:");
            System.exit(0);
        }
        System.setProperty(GraphSupplier.GRAPH_SUPPLIER_CLASS_PROPERTY, GRAPH_SUPPLIER_CLASS.getName());

        new ExecutorDelegation(args).run();
    }

    private <T> T extract(String param, Function<String, T> mapper, T defaultValue) {
        return Stream.of(args)
                .filter(arg -> arg.startsWith(param))
                .map(arg -> arg.substring(param.length()))
                .map(mapper)
                .findFirst()
                .orElse(defaultValue);
    }
}
