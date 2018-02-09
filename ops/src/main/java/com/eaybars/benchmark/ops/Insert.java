package com.eaybars.benchmark.ops;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.zip.GZIPInputStream;

public class Insert implements BiConsumer<Set<String>, GraphTraversalSource> {
    private File file;
    private int count;
    private int sampleInterval;
    private int commitInterval;

    private NumberFormat format = NumberFormat.getInstance(Locale.US);

    @Override
    public void accept(Set<String> args, GraphTraversalSource g) {
        if (!parseParameters(args)) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))))) {
            String line;
            long start = System.currentTimeMillis();
            long prevTime = 0;
            int count = 0;
            int prevCount = 0;
            System.out.println("Start time: " + start);
            System.out.println("Quantity\tTime\tRate/Sec");
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

                if (count % sampleInterval == 0) {
                    long time = System.currentTimeMillis();
                    System.out.println(count + "\t" + time + "\t" + format.format((count - prevCount) / (double) (time - prevTime) * 1000.0));
                    prevTime = time;
                    prevCount = count;
                }

                if (commitInterval == 1 || count % commitInterval == 0) {
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

            if (count % sampleInterval != 0) {
                long time = System.currentTimeMillis();
                System.out.println(count + "\t" + time + "\t" + format.format((count - prevCount) / (double) (time - prevTime) * 1000.0));
            }

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
            sampleInterval = args.stream()
                    .filter(s -> s.matches("s[0-9]+"))
                    .map(s -> s.substring(1))
                    .map(Integer::parseInt)
                    .findFirst()
                    .orElse(10000);
            commitInterval = args.stream()
                    .filter(s -> s.matches("c[0-9]+"))
                    .map(s -> s.substring(1))
                    .map(Integer::parseInt)
                    .findFirst()
                    .orElse(20);
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
