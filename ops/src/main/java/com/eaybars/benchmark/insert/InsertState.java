package com.eaybars.benchmark.insert;

import org.openjdk.jmh.annotations.*;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.*;
import java.util.zip.GZIPInputStream;

@State(Scope.Benchmark)
public class InsertState {
    public static final String REVIEW_FILE_PROPERTY = "InsertState.file.review";
    public static final String COMMIT_INTERVAL_PROPERTY = "InsertState.param.commitInterval";

    private BufferedReader reader;
    private JsonObject object;
    private int count;
    private int commitInterval;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        reader = new BufferedReader(new InputStreamReader(
                new GZIPInputStream(
                        new FileInputStream(System.getProperty(REVIEW_FILE_PROPERTY)))));
        commitInterval = Integer.parseInt(System.getProperty(COMMIT_INTERVAL_PROPERTY, "20"));
    }

    @Setup(Level.Invocation)
    public void advance() throws IOException {
        object = Json.createReader(new StringReader(reader.readLine())).readObject();
        count++;
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        reader.close();
        System.out.println("Count: "+count);
    }

    public JsonObject getObject() {
        return object;
    }

    public int getCount() {
        return count;
    }

    public int getCommitInterval() {
        return commitInterval;
    }
}
