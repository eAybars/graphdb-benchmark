package com.eaybars.benchmark.insert;

import com.eaybars.benchmark.GraphSupplier;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.*;
import java.util.function.Supplier;

public class Insert {
    private int measurementBatchSize;
    private int insertCount;

    private int maxInsertCount;

    public static Insert using(Class<? extends Supplier<GraphTraversalSource>> graphSupplierType) {
        System.setProperty(GraphSupplier.GRAPH_SUPPLIER_CLASS_PROPERTY, graphSupplierType.getName());
        return new Insert();
    }

    public Insert reviewsFile(String file) {
        File target = new File(file);
        if (target.exists()) {
            System.setProperty(InsertState.REVIEW_FILE_PROPERTY, target.getAbsolutePath());
            try (InputStream is = new BufferedInputStream(new FileInputStream(target))) {
                byte[] c = new byte[1024];
                int readChars;
                boolean charsAfterNewLine = false;
                while ((readChars = is.read(c)) != -1) {
                    charsAfterNewLine = true;
                    for (int i = 0; i < readChars; ++i) {
                        if (c[i] == '\n') {
                            charsAfterNewLine = false;
                            maxInsertCount++;
                        }
                    }
                }
                if (charsAfterNewLine) {
                    maxInsertCount++;
                }

            } catch (IOException e) {
                maxInsertCount = 900_000;
            }
        } else {
            throw new IllegalArgumentException("Review file not found: " + file);
        }
        return this;
    }

    public Insert commitInterval(int interval) {
        System.setProperty(InsertState.COMMIT_INTERVAL_PROPERTY, String.valueOf(interval));
        return this;
    }

    public Insert measurementBatchSize(int measurementBatchSize) {
        this.measurementBatchSize = measurementBatchSize;
        return this;
    }

    public Insert insertCount(int insertCount) {
        this.insertCount = insertCount;
        return this;
    }

    public void run() throws RunnerException {
        if (maxInsertCount == 0) {
            throw new IllegalStateException("Review source file is not set");
        }

        if (insertCount < 0) {
            insertCount = maxInsertCount;
        }

        int iterationCount = (int) Math.ceil(insertCount * 1.0/measurementBatchSize);
        if (iterationCount > maxInsertCount) {
            iterationCount = (int) Math.floor(maxInsertCount * 1.0/measurementBatchSize);
        }
        Options build = new OptionsBuilder()
                .include(InsertBenchmark.class.getName())
                .warmupIterations(0)
                .measurementIterations(iterationCount)
                .measurementBatchSize(measurementBatchSize)
                .forks(0)
                .build();

        new Runner(build).run();
    }

}
