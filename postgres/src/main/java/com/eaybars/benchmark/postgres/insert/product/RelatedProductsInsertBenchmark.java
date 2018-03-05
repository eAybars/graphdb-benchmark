package com.eaybars.benchmark.postgres.insert.product;

import com.eaybars.benchmark.insert.product.Products;
import com.eaybars.benchmark.postgres.insert.ConnectionSupplier;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import javax.json.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class RelatedProductsInsertBenchmark {

    @Benchmark
    public void benchmark(Products products, ConnectionSupplier connectionSupplier) throws SQLException {
        Connection connection = connectionSupplier.getConnection();
        JsonObject object = products.getObject();

        String sqlProductSelect = "SELECT product_id FROM product WHERE product_id=?";

        String sqlAlsoViewed = "INSERT INTO also_viewed(product_id,also_viewed_id) "
                + "VALUES(?,?)";
        String sqlBuyAfterViewing = "INSERT INTO buy_after_viewing(product_id,buy_after_viewing_id) "
                + "VALUES(?,?)";
        try (PreparedStatement productSelectStatement = connection.prepareStatement(sqlProductSelect);
             PreparedStatement alsoViewedStatement = connection.prepareStatement(sqlAlsoViewed);
             PreparedStatement buyAfterViewingStatement = connection.prepareStatement(sqlBuyAfterViewing);) {
            productSelectStatement.setString(1, object.getString("asin"));
            if (productSelectStatement.executeQuery().next()) {
                for (String alsoViewed : products.getAlsoViewed()) {
                    alsoViewedStatement.setString(1, object.getString("asin"));
                    alsoViewedStatement.setString(2, alsoViewed);
                    alsoViewedStatement.addBatch();
                }
                alsoViewedStatement.executeBatch();
                for (String buyAfterViewing : products.getBuyAfterViewing()) {
                    buyAfterViewingStatement.setString(1, object.getString("asin"));
                    buyAfterViewingStatement.setString(2, buyAfterViewing);
                    buyAfterViewingStatement.addBatch();

                }
                buyAfterViewingStatement.executeBatch();
            }

        }

        connectionSupplier.commit();
    }
}
