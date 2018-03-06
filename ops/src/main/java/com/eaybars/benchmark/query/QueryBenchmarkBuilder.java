package com.eaybars.benchmark.query;

import com.eaybars.benchmark.Arguments;
import com.eaybars.benchmark.BenchmarkRunner;
import com.eaybars.benchmark.Transaction;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class QueryBenchmarkBuilder implements BenchmarkRunner {
    private int times;
    private Transaction transaction;

    public QueryBenchmarkBuilder(int times) {
        this.times = times;
        transaction = new Transaction();
    }

    public static QueryBenchmarkBuilder times(int times) {
        return new QueryBenchmarkBuilder(times);
    }

    public QueryBenchmarkBuilder reinitialisePeriod(int period) {
        transaction.reinitialisePeriod(period);
        return this;
    }

    public void run(Class<?> benchmarkClass) throws Exception {
        transaction.done(benchmarkClass);
        Options build = new OptionsBuilder()
                .include(benchmarkClass.getSimpleName())
                .warmupIterations(0)
                .measurementIterations(times)
                .timeout(TimeValue.hours(4))
                .forks(Arguments.forks())
                .build();

        new Runner(build).run();

    }
}
