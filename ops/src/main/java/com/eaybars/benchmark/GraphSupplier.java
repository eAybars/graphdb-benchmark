package com.eaybars.benchmark;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.openjdk.jmh.annotations.*;

import java.util.function.Supplier;

@State(Scope.Thread)
public class GraphSupplier {
    public static final String GRAPH_SUPPLIER_CLASS_PROPERTY = "GraphSupplier.supplier.class";
    private GraphTraversalSource g;
    private Transaction.Options options;
    private int count;
    private int lastCommit;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        options = Transaction.currentOptions();
        initGraph();
    }

    private void initGraph() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        g = ((Supplier<GraphTraversalSource>) Class.forName(
                System.getProperty(GRAPH_SUPPLIER_CLASS_PROPERTY)).newInstance()).get();
    }

    @Setup(Level.Invocation)
    public void next() throws Exception {
        if (options.getGraphReinitialisationPeriod() > 0 &&
                count > 0 && count % options.getGraphReinitialisationPeriod() == 0) {
            tearDown();
            initGraph();
        } else {
            commit();
        }
        count++;
    }

    public int getCount() {
        return count;
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        try {
            if (options.getCommitInterval() > 0 && lastCommit < count) {
                g.getGraph().tx().commit();
            }
        } catch (Exception e) {
        } finally {
            g.getGraph().close();
        }
    }

    public void commit() {
        if (options.getCommitInterval() > 0 && count > 0 && lastCommit < count &&
                (options.getCommitInterval() == 1 || count % options.getCommitInterval() == 0)) {
            try {
                lastCommit = count;
                g.getGraph().tx().commit();
            } catch (Exception e) {
            }
        }
    }


    public GraphTraversalSource traversalSource() {
        return g;
    }

}
