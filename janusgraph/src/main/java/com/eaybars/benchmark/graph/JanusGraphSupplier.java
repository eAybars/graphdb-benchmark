package com.eaybars.benchmark.graph;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.*;
import org.janusgraph.core.schema.JanusGraphManagement;

import java.util.function.Supplier;

public class JanusGraphSupplier implements Supplier<Graph> {
    @Override
    public Graph get() {
        JanusGraph graph = JanusGraphFactory.build()
                .set("storage.backend", "cassandra")
                .set("storage.hostname", "cassandra")
                .set("storage.cassandra.keyspace", "test")
                .set("index.search.backend", "elasticsearch")
                .set("index.search.hostname", "elasticsearch")
                .set("index.search.index-name", "test")
                .set("cache.db-cache", true)
                .set("cache.db-cache-clean-wait", 20)
                .set("cache.db-cache-time", 180000)
                .set("cache.db-cache-size", 0.1)
                .set("schema.default", "tp3")
                .open();

        JanusGraphManagement management = graph.openManagement();
        management.makeEdgeLabel("created").multiplicity(Multiplicity.MULTI).make();
        management.makeEdgeLabel("about").multiplicity(Multiplicity.SIMPLE).make();

        management.makeVertexLabel("person").make();
        management.makeVertexLabel("review").make();
        management.makeVertexLabel("product").make();

        PropertyKey userId = management.makePropertyKey("reviewerID").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        PropertyKey productId = management.makePropertyKey("productId").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        PropertyKey overall = management.makePropertyKey("overall").dataType(Double.class).cardinality(Cardinality.SINGLE).make();
        PropertyKey unixReviewTime = management.makePropertyKey("unixReviewTime").dataType(Long.class).cardinality(Cardinality.SINGLE).make();

        management.buildIndex("userIdIndex", Vertex.class).addKey(userId).buildCompositeIndex();
        management.buildIndex("productIdIndex", Vertex.class).addKey(productId).buildCompositeIndex();
        management.buildIndex("overallFieldIndex", Vertex.class).addKey(overall).buildMixedIndex("search");
        management.buildIndex("unixReviewTimeIndex", Vertex.class).addKey(unixReviewTime).buildMixedIndex("search");

        management.commit();

        return graph;
    }
}
