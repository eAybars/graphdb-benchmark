package com.eaybars.benchmark.graph;


import com.steelbridgelabs.oss.neo4j.structure.Neo4JElementIdProvider;
import com.steelbridgelabs.oss.neo4j.structure.Neo4JGraph;
import com.steelbridgelabs.oss.neo4j.structure.providers.Neo4JNativeElementIdProvider;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.neo4j.driver.v1.*;

import java.util.function.Supplier;

public class Neo4jDbSupplier implements Supplier<GraphTraversalSource> {
    private static boolean initializedBefore = false;
    private static final Driver DRIVER = GraphDatabase.driver("bolt://neo4j", (AuthToken) null);
    private static final Neo4JElementIdProvider<?> VERTEX_ID_PROVIDER = new Neo4JNativeElementIdProvider();
    private static final Neo4JElementIdProvider<?> EDGE_ID_PROVIDER = new Neo4JNativeElementIdProvider();

    @Override
    public GraphTraversalSource get() {
        if (!initializedBefore) {
            initializedBefore = true;
            final Session session = DRIVER.session();
            session.run("CREATE INDEX ON :created(unixReviewTime)");
            session.run("CREATE INDEX ON :person(reviewerID)");
            session.run("CREATE INDEX ON :review(overall)");
            session.run("CREATE INDEX ON :review(unixReviewTime)");
            session.run("CREATE INDEX ON :product(productId)");
            session.run("CREATE INDEX ON :product(price)");
            session.run("CREATE INDEX ON :category(categoryName)");
            session.close();
        }
        final Neo4JGraph neo4JGraph = new Neo4JGraph(DRIVER, VERTEX_ID_PROVIDER, EDGE_ID_PROVIDER);
        return neo4JGraph.traversal();
    }

}
