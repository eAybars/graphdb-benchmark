package com.eaybars.benchmark;

import java.util.function.Consumer;

public class BenchmarkRunnerConsumer implements Consumer<BenchmarkRunner> {
    private Class<?> benchmarkClass;

    public BenchmarkRunnerConsumer(Class<?> benchmarkClass) {
        this.benchmarkClass = benchmarkClass;
    }

    public static Consumer<BenchmarkRunner> forBenchmark(Class<?> benchmarkClass) {
        return new BenchmarkRunnerConsumer(benchmarkClass);
    }

    @Override
    public void accept(BenchmarkRunner r) {
        try {
            r.run(benchmarkClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
