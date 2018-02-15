package com.eaybars.benchmark.insert.product;

import com.eaybars.benchmark.insert.Insert;
import com.eaybars.benchmark.insert.review.ReviewsInsertBenchmark;
import org.openjdk.jmh.annotations.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.stream.JsonParsingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@State(Scope.Benchmark)
public class Products {
    private Insert insert;
    private BufferedReader reader;
    private JsonObject object;
    private int count;

    private Set<String> alsoViewed;
    private Set<String> buyAfterViewing;
    private Set<String> categories;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        insert = Insert.currentFor(ReviewsInsertBenchmark.class);
        reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(insert.getSource().inputStream())));
    }

    @Setup(Level.Invocation)
    public void next() throws IOException {
        boolean parsed;
        do {
            try {
                String line = reader.readLine();
                //correct the format of the source file as much as possible
                String updatedLine = line
                        .replace('\\',' ')
                        .replaceAll("([,.])\"", "'")
                        .replace('"', '\'')
                        .replaceAll("(([:,]\\s)|[\\[{,:])'", "$1\"")
                        .replaceAll("'([:}\\],])", "\"$1");
                object = Json.createReader(new StringReader(updatedLine)).readObject();
                alsoViewed = collectRelatedProducts("also_viewed");
                buyAfterViewing = collectRelatedProducts("buy_after_viewing");
                categories = collectCategories();
                count++;
                parsed = true;
            } catch (JsonParsingException e) {
                parsed = false;
            }
        } while (!parsed);
    }

    private Set<String> collectRelatedProducts(String field) {
        JsonObject relatedProducts = object.getJsonObject("related");
        if (relatedProducts != null) {
            JsonArray relatedItems = relatedProducts.getJsonArray(field);
            if (relatedItems != null) {
                return relatedItems.stream()
                        .map(JsonString.class::cast)
                        .map(JsonString::getString)
                        .collect(Collectors.toSet());
            }
        }
        return Collections.emptySet();
    }

    private Set<String> collectCategories() {
        JsonArray categories = object.getJsonArray("categories");
        return categories.stream()
                .map(JsonArray.class::cast)
                .flatMap(JsonArray::stream)
                .map(JsonString.class::cast)
                .map(JsonString::getString)
                .collect(Collectors.toSet());
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        reader.close();
    }

    public JsonObject getObject() {
        return object;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public Set<String> getAlsoViewed() {
        return alsoViewed;
    }

    public Set<String> getBuyAfterViewing() {
        return buyAfterViewing;
    }

    public int getCount() {
        return count;
    }

    public int getCommitInterval() {
        return insert.getCommitInterval();
    }
}
