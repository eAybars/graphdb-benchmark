package com.eaybars.benchmark.insert.review;

import com.eaybars.benchmark.insert.Insert;
import org.openjdk.jmh.annotations.*;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.*;
import java.util.zip.GZIPInputStream;

@State(Scope.Benchmark)
public class Reviews {
    private Insert insert;
    private BufferedReader reader;
    private JsonObject object;
    private int count;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        insert = Insert.currentFor(ReviewsInsertBenchmark.class);
        reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(insert.getSource().inputStream())));
    }

    @Setup(Level.Invocation)
    public void next() throws IOException {
        object = Json.createReader(new StringReader(reader.readLine())).readObject();
        count++;
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        reader.close();
    }

    public JsonObject getObject() {
        return object;
    }

    public int getCount() {
        return count;
    }

    public int getCommitInterval() {
        return insert.getCommitInterval();
    }
}
