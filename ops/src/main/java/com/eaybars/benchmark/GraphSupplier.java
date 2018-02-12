package com.eaybars.benchmark;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.openjdk.jmh.annotations.*;

import java.util.function.Supplier;

@State(Scope.Thread)
public class GraphSupplier {
    public static final String GRAPH_SUPPLIER_CLASS_PROPERTY = "GraphSupplier.supplier.class";
    private GraphTraversalSource g;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        g = ((Supplier<GraphTraversalSource>)Class.forName(
                System.getProperty(GRAPH_SUPPLIER_CLASS_PROPERTY)).newInstance()).get();
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        try {
            g.getGraph().tx().commit();
        } catch (Exception e) {}
        finally {
            g.getGraph().close();
        }
    }

    public GraphTraversalSource traversalSource() {
        return g;
    }

}
