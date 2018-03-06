package com.eaybars.benchmark.insert.product;

import com.eaybars.benchmark.insert.ConnectionSupplier;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import javax.json.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class RelatedProductsInsertSQLBenchmark {

    @Benchmark
    public void benchmark(Products products, ConnectionSupplier connectionSupplier) throws SQLException {
        Connection connection = connectionSupplier.getConnection();
        JsonObject object = products.getObject();

        String sqlProductSelect = "SELECT product_id FROM product WHERE product_id=?";

        String sqlAlsoViewed = "INSERT INTO also_viewed(product_id,also_viewed_id) "
                + "VALUES(?,?)";
        String sqlBuyAfterViewing = "INSERT INTO buy_after_viewing(product_id,buy_after_viewing_id) "
                + "VALUES(?,?)";
        boolean executeAlsoviewed = false, executeBuyAfter = false;
        try (PreparedStatement productSelectStatement = connection.prepareStatement(sqlProductSelect);
             PreparedStatement alsoViewedStatement = connection.prepareStatement(sqlAlsoViewed);
             PreparedStatement buyAfterViewingStatement = connection.prepareStatement(sqlBuyAfterViewing);) {
            productSelectStatement.setString(1, object.getString("asin"));
            ResultSet mainProduct = productSelectStatement.executeQuery();
            if (mainProduct.next()) {
                mainProduct.close();
                for (String alsoViewed : products.getAlsoViewed()) {
                    productSelectStatement.setString(1, alsoViewed);
                    ResultSet alsoViewedResult = productSelectStatement.executeQuery();
                    if (alsoViewedResult.next()) {
                        alsoViewedStatement.setString(1, object.getString("asin"));
                        alsoViewedStatement.setString(2, alsoViewed);
                        alsoViewedStatement.addBatch();
                        executeAlsoviewed = true;
                    }
                    alsoViewedResult.close();
                }
                if (executeAlsoviewed) {
                    alsoViewedStatement.executeBatch();
                }

                for (String buyAfterViewing : products.getBuyAfterViewing()) {
                    productSelectStatement.setString(1, buyAfterViewing);
                    ResultSet buyAfterViewingResult = productSelectStatement.executeQuery();
                    if (buyAfterViewingResult.next()) {
                        buyAfterViewingStatement.setString(1, object.getString("asin"));
                        buyAfterViewingStatement.setString(2, buyAfterViewing);
                        buyAfterViewingStatement.addBatch();
                        executeBuyAfter = true;
                    }
                }
                if (executeBuyAfter) {
                    buyAfterViewingStatement.executeBatch();
                }
            }

        }

        connectionSupplier.commit();
    }
}
