package com.eaybars.benchmark.postgres.insert;

import org.openjdk.jmh.annotations.*;

import java.sql.Connection;
import java.sql.SQLException;

@State(Scope.Thread)
public class ConnectionSupplier {
    private Connection connection;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        //TODO initialize connection
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        try {
            getConnection().commit();
        } catch (SQLException e) {
        }
        getConnection().close();
    }


    public Connection getConnection() {
        return connection;
    }
}
