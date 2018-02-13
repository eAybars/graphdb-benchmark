package com.eaybars.benchmark.insert;

import com.eaybars.benchmark.Information;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.io.Serializable;

public class Insert implements Serializable {
    private InsertSource source;
    private int commitInterval;
    private int measurementBatchSize;
    private int insertCount;

    private boolean sealed;

    public static Insert fromFile(String file) {
        Insert insert = new Insert();
        try {
            insert.source = InsertSource.from(file);
            if (!insert.source.exists()) {
                throw new IllegalArgumentException("Review file not found: " + file);
            } else if (insert.source.getNumberOfLines() == 0) {
                throw new IllegalArgumentException("Review file is empty: " + file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return insert;
    }

    public Insert commitInterval(int interval) {
        if (sealed) {
            throw new IllegalStateException();
        }
        this.commitInterval = interval;
        return this;
    }

    public Insert measurementBatchSize(int measurementBatchSize) {
        if (sealed) {
            throw new IllegalStateException();
        }
        this.measurementBatchSize = measurementBatchSize;
        return this;
    }

    public Insert insertCount(int insertCount) {
        if (sealed) {
            throw new IllegalStateException();
        }
        this.insertCount = insertCount;
        return this;
    }

    public static Insert currentFor(Class<?> benchmarkClass) {
        try {
            return Information.SOURCE.load(Insert.class, benchmarkClass.getName());
        } catch (IOException e) {
            return null;
        }
    }

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

    public void run(Class<?> benchmarkClass) throws Exception {
        if (insertCount < 0) {
            insertCount = source.getNumberOfLines();
        }

        sealed = true;

        Information.SOURCE.save(benchmarkClass.getName(), this);

        int iterationCount = (int) Math.floor(source.getNumberOfLines() * 1.0 / measurementBatchSize);

        Options build = new OptionsBuilder()
                .include(benchmarkClass.getName())
                .warmupIterations(0)
                .measurementIterations(iterationCount)
                .measurementBatchSize(measurementBatchSize)
                .forks(0)
                .build();

        new Runner(build).run();
    }

}
