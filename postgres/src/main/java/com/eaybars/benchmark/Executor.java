package com.eaybars.benchmark;


import com.eaybars.benchmark.insert.Insert;
import com.eaybars.benchmark.postgres.insert.product.*;
import com.eaybars.benchmark.postgres.insert.review.ReviewsInsertBenchmark;

import java.util.function.Function;
import java.util.stream.Stream;

public class Executor {
    private String[] args;

    private Executor(String[] args) {
        this.args = args;
    }

    private void run() throws Exception {
        checkAndRunInsert(ProductsInsertBenchmark.class, "-insert.product.", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz");
        checkAndRunInsert(ReviewsInsertBenchmark.class, "-insert.review.", "/opt/graphdb-benchmark/reviews_Kindle_Store_5.json.gz");
        checkAndRunInsert(CategoryInsertBenchmark.class, "-insert.productCategory.", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz");
        checkAndRunInsert(RelatedProductsInsertBenchmark.class, "-insert.relatedProduct.", "/opt/graphdb-benchmark/meta_Kindle_Store.json.gz");
    }

    public static void main(String[] args) throws Exception {
        new Executor(args).run();
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

    private <T> T extract(String param, Function<String, T> mapper, T defaultValue) {
        return Stream.of(args)
                .filter(arg -> arg.startsWith(param))
                .map(arg -> arg.substring(param.length()))
                .map(mapper)
                .findFirst()
                .orElse(defaultValue);
    }
}
