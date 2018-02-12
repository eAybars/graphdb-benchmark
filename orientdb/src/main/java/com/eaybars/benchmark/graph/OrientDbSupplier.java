package com.eaybars.benchmark.graph;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.ODatabaseType;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.tinkerpop.gremlin.orientdb.OrientGraph;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;

import java.util.function.Supplier;

public class OrientDbSupplier implements Supplier<GraphTraversalSource> {
    @Override
    public GraphTraversalSource get() {
        OrientDB orient = new OrientDB("remote:orientdb", "root", "orientdb", OrientDBConfig.defaultConfig());
        orient.create("test", ODatabaseType.PLOCAL);
        ODatabaseSession db = orient.open("test", "admin", "admin");

        if (db.getClass("created") == null) {
            OClass created = db.createEdgeClass("created");
            db.createEdgeClass("about");

            OClass person = db.createVertexClass("person");
            OClass review = db.createVertexClass("review");
            OClass product = db.createVertexClass("product");

            person.createProperty("reviewerID", OType.STRING);
            product.createProperty("productId", OType.STRING);
            review.createProperty("overall", OType.DOUBLE);
            review.createProperty("unixReviewTime", OType.LONG);
            created.createProperty("unixReviewTime", OType.LONG);

            person.createIndex("userIdIndex", OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "reviewerID");
            product.createIndex("productIdIndex", OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX, "productId");
            review.createIndex("overallFieldIndex", OClass.INDEX_TYPE.NOTUNIQUE, "overall");
            review.createIndex("review_unixReviewTimeIndex", OClass.INDEX_TYPE.NOTUNIQUE, "unixReviewTime");
            created.createIndex("created_overallFieldIndex", OClass.INDEX_TYPE.NOTUNIQUE, "unixReviewTime");
        }

        db.close();
        orient.close();
        return OrientGraph.open("remote:orientdb/test").traversal();
    }
}
