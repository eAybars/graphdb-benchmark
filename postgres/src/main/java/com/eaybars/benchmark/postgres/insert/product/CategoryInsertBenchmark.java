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
public class CategoryInsertBenchmark {

    @Benchmark
    public void benchmark(Products products, ConnectionSupplier connectionSupplier) {
        Connection connection = connectionSupplier.getConnection();
        JsonObject object = products.getObject();
        String sqlInsertToCategory = "INSERT INTO category(category_name) VALUES(?)";
        String sqlInsertToProductCategory = "INSERT INTO product_category(product_id, category_name) VALUES(?,?)";
        try (PreparedStatement preparedStatementCategory = connection.prepareStatement(sqlInsertToCategory);
             PreparedStatement preparedStatementProductCategory = connection.prepareStatement(sqlInsertToProductCategory);) {
            for (String category : products.getCategories()) {
                preparedStatementCategory.setString(1, category);
                preparedStatementCategory.addBatch();

                preparedStatementProductCategory.setString(1, object.getString("asin"));
                preparedStatementProductCategory.setString(2, category);
                preparedStatementProductCategory.addBatch();
            }
            preparedStatementCategory.executeBatch();
            preparedStatementProductCategory.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connectionSupplier.commit();

    }
}
