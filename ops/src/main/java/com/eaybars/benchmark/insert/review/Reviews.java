package com.eaybars.benchmark.insert.review;

import com.eaybars.benchmark.insert.Insert;
import org.openjdk.jmh.annotations.*;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

@State(Scope.Benchmark)
public class Reviews {
    private Insert.Options options;
    private BufferedReader reader;
    private JsonObject object;
    private int count;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        options = Insert.currentInsertOptions();
        reader = new BufferedReader(new InputStreamReader(options.getSource().inputStream()));
        for (int i = 0; i < options.getStartFrom(); i++) {
            next();
        }
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
        return options.getCommitInterval();
    }
}
