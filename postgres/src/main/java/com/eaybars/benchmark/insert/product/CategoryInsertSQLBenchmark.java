package com.eaybars.benchmark.insert.product;

import com.eaybars.benchmark.ConnectionSupplier;
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
public class CategoryInsertSQLBenchmark {

    @Benchmark
    public void benchmark(Products products, ConnectionSupplier connectionSupplier) throws SQLException {
        Connection connection = connectionSupplier.getConnection();
        JsonObject object = products.getObject();
        String sqlProductSelect = "SELECT product_id FROM product WHERE product_id=?";
        String sqlCategorySelect = "SELECT category_name FROM category WHERE category_name=?";
        String sqlInsertToCategory = "INSERT INTO category(category_name) VALUES(?)";
        String sqlInsertToProductCategory = "INSERT INTO product_category(product_id, category_name) VALUES(?,?)";
        try (PreparedStatement productSelectStatement = connection.prepareStatement(sqlProductSelect);
             PreparedStatement preparedStatementSelectCategory = connection.prepareStatement(sqlCategorySelect);
             PreparedStatement preparedStatementInsertCategory = connection.prepareStatement(sqlInsertToCategory);
             PreparedStatement preparedStatementInsertProductCategory = connection.prepareStatement(sqlInsertToProductCategory);) {
            productSelectStatement.setString(1, object.getString("asin"));
            if (productSelectStatement.executeQuery().next()) {
                boolean executeCategoryInsert = false;
                for (String category : products.getCategories()) {
                    preparedStatementSelectCategory.setString(1, category);
                    if (!preparedStatementSelectCategory.executeQuery().next()) {
                        preparedStatementInsertCategory.setString(1, category);
                        preparedStatementInsertCategory.addBatch();
                        executeCategoryInsert = true;
                    }

                    preparedStatementInsertProductCategory.setString(1, object.getString("asin"));
                    preparedStatementInsertProductCategory.setString(2, category);
                    preparedStatementInsertProductCategory.addBatch();
                }

                if (executeCategoryInsert) {
                    preparedStatementInsertCategory.executeBatch();
                }

                preparedStatementInsertProductCategory.executeBatch();
            }
        }
        connectionSupplier.commit();

    }
}
