package com.eaybars.benchmark.insert;

import com.eaybars.benchmark.ExecutorDelegation;
import com.eaybars.benchmark.Information;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.io.Serializable;

public class Insert implements Serializable, Cloneable {
    private static final String CURRENT_OPTIONS_NAME = "com.eaybars.benchmark.graph.insert.currentoptions";
    private Options options = new Options();

    public static Insert fromFile(String file) {
        Insert insert = new Insert();
        try {
            insert.options.source = InsertSource.from(file);
            if (!insert.options.source.exists()) {
                throw new IllegalArgumentException("Review file not found: " + file);
            } else if (insert.options.source.getNumberOfLines() == 0) {
                throw new IllegalArgumentException("Review file is empty: " + file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return insert;
    }

    public Insert commitInterval(int interval) {
        this.options.commitInterval = interval;
        return this;
    }

    public Insert measurementBatchSize(int measurementBatchSize) {
        this.options.measurementBatchSize = measurementBatchSize;
        return this;
    }

    public Insert insertCount(int insertCount) {
        this.options.insertCount = insertCount;
        return this;
    }

    public Insert startingFrom(int startFrom) {
        this.options.startFrom = startFrom;
        return this;
    }

    public static Options currentInsertOptions() {
        try {
            return Information.BROKER.load(Options.class, System.getProperty(CURRENT_OPTIONS_NAME, "~none"));
        } catch (IOException e) {
            return null;
        }
    }

    public Options getOptions() {
        return options;
    }

    public void run(Class<?> benchmarkClass) throws Exception {
        if (options.insertCount < 0) {
            options.insertCount = options.source.getNumberOfLines() - options.startFrom;
        }

        Options clone = options.clone();

        String name = benchmarkClass.getName() + ".Options";
        System.setProperty(CURRENT_OPTIONS_NAME, name);

        Information.BROKER.save(name, clone);

        int excess = Math.max(clone.startFrom + clone.insertCount - clone.source.getNumberOfLines(), 0);

        int iterationCount = (int) Math.floor((clone.insertCount - excess) * 1.0 / clone.measurementBatchSize);

        org.openjdk.jmh.runner.options.Options build = new OptionsBuilder()
                .include(benchmarkClass.getName())
                .warmupIterations(0)
                .measurementIterations(iterationCount)
                .measurementBatchSize(clone.measurementBatchSize)
                .forks(ExecutorDelegation.forks())
                .build();

        new Runner(build).run();
    }

    public static class Options implements Serializable, Cloneable {
        private InsertSource source;
        private int commitInterval;
        private int measurementBatchSize;
        private int insertCount;
        private int startFrom;

        public InsertSource getSource() {
            return source;
        }

        public int getCommitInterval() {
            return commitInterval;
        }

        public int getMeasurementBatchSize() {
            return measurementBatchSize;
        }

        public int getInsertCount() {
            return insertCount;
        }

        public int getStartFrom() {
            return startFrom;
        }

        @Override
        public Options clone() {
            try {
                return (Options) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new InternalError(e);
            }
        }
    }

}
