package com.eaybars.benchmark;

import com.eaybars.benchmark.insert.Insert;
import com.eaybars.benchmark.query.QueryBenchmarkBuilder;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class Arguments {
    private String[] args;

    public static final Arguments ARGUMENTS = new Arguments();

    void apply(String[] args) {
        this.args = args;
        System.out.println("Running benchmarks with the following arguments: "+ Arrays.toString(args));
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

    public Optional<Insert> insertFromArguments(String name, String defaultFile) {
        if (extract("-insert."+name+".", s -> true, false)) {
            Insert insert = Insert.fromFile(extract(toInsertParam(name, "file"), Function.identity(), defaultFile))
                    .insertCount(extract(toInsertParam(name, "count"), Integer::parseInt, -1))
                    .startingFrom(extract(toInsertParam(name, "start"), Integer::parseInt, 0))
                    .measurementBatchSize(extract(toInsertParam(name, "measurementBatch"), Integer::parseInt, 1000))
                    .commitInterval(extract(toInsertParam(name, "commit"), Integer::parseInt, 20))
                    .reinitialisePeriod(extract(toInsertParam(name, "reinitGraph"), Integer::parseInt, 0));
            return Optional.of(insert);
        }
        return Optional.empty();
    }

    private String toInsertParam(String name, String param) {
        return "-insert." + name + "." + param + "=";
    }

    public Optional<QueryBenchmarkBuilder> queryBenchmarkFromArguments(String name) {
        int times = extract("-query."+name+"=", Integer::parseInt, 0);
        if (times > 0) {
            QueryBenchmarkBuilder qb = QueryBenchmarkBuilder.times(times)
                    .reinitialisePeriod(extract("-query."+name+".reinitGraph=", Integer::parseInt, 0));
            return Optional.of(qb);
        }
        return Optional.empty();
    }

}
