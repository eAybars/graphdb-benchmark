package com.eaybars.benchmark.insert.product;

import com.eaybars.benchmark.ConnectionSupplier;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import javax.json.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
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
        try (PreparedStatement productSelectStatement = connection.prepareStatement(sqlProductSelect);
             PreparedStatement alsoViewedStatement = connection.prepareStatement(sqlAlsoViewed);
             PreparedStatement buyAfterViewingStatement = connection.prepareStatement(sqlBuyAfterViewing);) {
            productSelectStatement.setString(1, object.getString("asin"));
            ResultSet mainProduct = productSelectStatement.executeQuery();
            if (mainProduct.next()) {
                mainProduct.close();
                insertRelatedProducts(productSelectStatement, alsoViewedStatement, object.getString("asin"), products.getAlsoViewed());
                insertRelatedProducts(productSelectStatement, buyAfterViewingStatement, object.getString("asin"), products.getBuyAfterViewing());
            }
        }

        connectionSupplier.commit();
    }

    private void insertRelatedProducts(PreparedStatement select,
                                       PreparedStatement insert,
                                       String product,
                                       Set<String> relatedProducts) throws SQLException {
        boolean executeBatch = false;
        for (String relatedProduct : relatedProducts) {
            select.setString(1, relatedProduct);
            ResultSet selectResult = select.executeQuery();

            if (selectResult.next()) {
                insert.setString(1, product);
                insert.setString(2, relatedProduct);
                insert.addBatch();
                executeBatch = true;
            }
            selectResult.close();
        }
        if (executeBatch) {
            insert.executeBatch();
        }
    }
}
