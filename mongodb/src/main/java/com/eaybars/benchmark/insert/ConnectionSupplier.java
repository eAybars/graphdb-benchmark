package com.eaybars.benchmark.insert;

import com.eaybars.benchmark.Transaction;
import org.openjdk.jmh.annotations.*;

import java.sql.Connection;

@State(Scope.Thread)
public class ConnectionSupplier {
    private Connection connection;
    private Transaction.Options options;
    private int count;
    private int lastCommit;


    @Setup(Level.Trial)
    public void setUp() throws Exception {
        options = Transaction.currentOptions();
        initConnection();
    }

    private void initConnection() {


    }


    @Setup(Level.Invocation)
    public void next() throws Exception {
        if (options.getGraphReinitialisationPeriod() > 0 &&
                count > 0 && count % options.getGraphReinitialisationPeriod() == 0) {
            tearDown();
            initConnection();
        } else {
            commit();
        }
        count++;
    }

    public int getCount() {
        return count;
    }


    public void commit() {
        if (options.getCommitInterval() > 0 && count > 0 && lastCommit < count &&
                (options.getCommitInterval() == 1 || count % options.getCommitInterval() == 0)) {
            try {
                connection.commit();
                lastCommit = count;
            } catch (Exception e) {
            }
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        try {
            if (options.getCommitInterval() > 0 && lastCommit < count) {
                connection.commit();
                lastCommit = count;
            }
        } catch (Exception e) {
        } finally {
            connection.close();
        }
    }


    public Connection getConnection() {
        return connection;
    }
}
