package com.eaybars.benchmark.graph;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.apache.tinkerpop.gremlin.util.config.YamlConfiguration;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class JanusGraphSnSupplier implements Supplier<GraphTraversalSource> {
    private static final Logger LOGGER = Logger.getLogger(JanusGraphSnSupplier.class.getName());
    private static Configuration conf;

    static {
        try {
            conf = new PropertiesConfiguration(JanusGraphSnSupplier.class.getClassLoader().getResource(""));
            YamlConfiguration yc = new YamlConfiguration();
            yc.load(JanusGraphSnSupplier.class.getClassLoader().getResource("remote-objects.yaml"));
            Cluster cluster = Cluster.open(yc);
            Client client = cluster.connect();

            client.submit(getSchemeString()).stream()
                    .map(Result::toString).forEach(LOGGER::info);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GraphTraversalSource get() {
        return EmptyGraph.instance().traversal().withRemote(conf);
    }

    private static String getSchemeString() {
        StringBuilder sb = new StringBuilder();

        sb.append("JanusGraphManagement management = graph.openManagement(); ");
        sb.append("if (graph.getEdgeLabel(\"created\") == null) { ");
        sb.append("management.makeEdgeLabel(\"created\").multiplicity(Multiplicity.MULTI).make(); ");
        sb.append("management.makeEdgeLabel(\"about\").multiplicity(Multiplicity.SIMPLE).make(); ");
        sb.append("management.makeEdgeLabel(\"also_viewed\").multiplicity(Multiplicity.MULTI).make(); ");
        sb.append("management.makeEdgeLabel(\"buy_after_viewing\").multiplicity(Multiplicity.MULTI).make(); ");
        sb.append("management.makeEdgeLabel(\"productCategory\").multiplicity(Multiplicity.MULTI).make(); ");
        sb.append("management.makeVertexLabel(\"person\").make(); ");
        sb.append("management.makeVertexLabel(\"review\").make(); ");
        sb.append("management.makeVertexLabel(\"product\").make(); ");
        sb.append("management.makeVertexLabel(\"category\").make(); ");
        sb.append("PropertyKey categoryName = management.makePropertyKey(\"categoryName\").dataType(String.class).cardinality(Cardinality.SINGLE).make(); ");
        sb.append("PropertyKey userId = management.makePropertyKey(\"reviewerID\").dataType(String.class).cardinality(Cardinality.SINGLE).make(); ");
        sb.append("PropertyKey productId = management.makePropertyKey(\"productId\").dataType(String.class).cardinality(Cardinality.SINGLE).make(); ");
        sb.append("PropertyKey price = management.makePropertyKey(\"price\").dataType(Double.class).cardinality(Cardinality.SINGLE).make(); ");
        sb.append("PropertyKey overall = management.makePropertyKey(\"overall\").dataType(Double.class).cardinality(Cardinality.SINGLE).make(); ");
        sb.append("PropertyKey unixReviewTime = management.makePropertyKey(\"unixReviewTime\").dataType(Long.class).cardinality(Cardinality.SINGLE).make(); ");
        sb.append("management.buildIndex(\"categoryNameIndex\", Vertex.class).addKey(categoryName).buildCompositeIndex();");
        sb.append("management.buildIndex(\"userIdIndex\", Vertex.class).addKey(userId).buildCompositeIndex();");
        sb.append("management.buildIndex(\"productIdIndex\", Vertex.class).addKey(productId).buildCompositeIndex();");
        sb.append("management.buildIndex(\"overallFieldIndex\", Vertex.class).addKey(overall).buildMixedIndex(\"search\"); ");
        sb.append("management.buildIndex(\"priceFieldIndex\", Vertex.class).addKey(price).buildMixedIndex(\"search\"); ");
        sb.append("management.buildIndex(\"unixReviewTimeIndex\", Vertex.class).addKey(unixReviewTime).buildMixedIndex(\"search\"); ");
        sb.append("management.commit(); ");
        sb.append("}");

        return sb.toString();
    }
}
