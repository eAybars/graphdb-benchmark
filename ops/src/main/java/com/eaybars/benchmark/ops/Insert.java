package com.eaybars.benchmark.ops;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.*;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.zip.GZIPInputStream;

public class Insert implements BiConsumer<Set<String>, GraphTraversalSource> {
    private File file;
    private int count;

    @Override
    public void accept(Set<String> args, GraphTraversalSource g) {
        if (!parseParameters(args)) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))))) {
            String line;
            long start = System.currentTimeMillis();
            System.out.println("Start time: " + start);
            int count = 0;
            while ((line = reader.readLine()) != null && count < this.count) {
                count++;
                JsonObject object = Json.createReader(new StringReader(line)).readObject();
                Vertex review = g.getGraph().addVertex("review");
                review.property("reviewText", object.getString("reviewText"));
                review.property("overall", object.getJsonNumber("overall").doubleValue());
                review.property("summary", object.getString("summary"));
                review.property("unixReviewTime", object.getJsonNumber("unixReviewTime").longValue());

                Vertex user;
                try {
                    user = g.V().hasLabel("person").has("reviewerID", object.getString("reviewerID")).next();
                } catch (NoSuchElementException e) {
                    user = g.addV("person")
                            .property("reviewerID", object.getString("reviewerID"))
                            .property("reviewerName", object.getString("reviewerName", "anonymous"))
                            .next();
                }

                Vertex product;
                try {
                    product = g.V().hasLabel("product").has("productId", object.getString("asin")).next();
                } catch (NoSuchElementException e) {
                    product = g.addV("product")
                            .property("productId", object.getString("asin"))
                            .next();
                }


                user.addEdge("created", review, "unixReviewTime", object.getJsonNumber("unixReviewTime").longValue());
                review.addEdge("about", product);

                if (count % 10000 == 0) {
                    System.out.println((System.currentTimeMillis() - start) + " Total inserted: " + count);
                }

                if (count % 20 == 0) {
                    try {
                        g.getGraph().tx().commit();
                    } catch (Exception e) {
                    }
                }
            }

            try {
                g.getGraph().tx().commit();
            } catch (Exception e) {
            }


            System.out.println((System.currentTimeMillis() - start) + " Total inserted: " + count);
            System.out.println("Total Time: " + (System.currentTimeMillis() - start));
        } catch (IOException e) {
            System.out.println("Insert failed: ");
            e.printStackTrace();
        }
    }

    private boolean parseParameters(Set<String> args) {
        if (args.contains("insert")) {
            count = args.stream().mapToInt(Insert::safeParse).filter(v -> v > 0).max().orElse(Integer.MAX_VALUE);
            file = args.stream().filter(s -> s.endsWith(".json.gz"))
                    .map(File::new)
                    .filter(File::exists)
                    .findFirst().orElse(new File("reviews_Kindle_Store_5.json.gz"));
            if (!file.exists()) {
                System.out.println("Insert file not found: " + file);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    private static int safeParse(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}