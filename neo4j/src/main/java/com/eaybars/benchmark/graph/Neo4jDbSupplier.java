package com.eaybars.benchmark.graph;


import com.steelbridgelabs.oss.neo4j.structure.Neo4JElementIdProvider;
import com.steelbridgelabs.oss.neo4j.structure.Neo4JGraph;
import com.steelbridgelabs.oss.neo4j.structure.providers.Neo4JNativeElementIdProvider;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import java.util.function.Supplier;

public class Neo4jDbSupplier implements Supplier<GraphTraversalSource> {

    @Override
    public GraphTraversalSource get() {
        final Driver driver = GraphDatabase.driver("bolt://neo4j", (AuthToken) null);
        final Session session = driver.session();
        session.run("CREATE INDEX ON :created(unixReviewTime)");
        session.run("CREATE INDEX ON :person(reviewerID)");
        session.run("CREATE INDEX ON :review(overall)");
        session.run("CREATE INDEX ON :review(unixReviewTime)");
        session.run("CREATE INDEX ON :product(productId)");
        session.run("CREATE INDEX ON :product(price)");
        session.run("CREATE INDEX ON :category(categoryName)");
        final Neo4JElementIdProvider<?> vertexIdProvider = new Neo4JNativeElementIdProvider();
        final Neo4JElementIdProvider<?> edgeIdProvider = new Neo4JNativeElementIdProvider();
        final Neo4JGraph neo4JGraph = new Neo4JGraph(driver, vertexIdProvider, edgeIdProvider);
        return neo4JGraph.traversal();
    }

}
