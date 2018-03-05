package com.eaybars.benchmark.postgres.insert.product;

import com.eaybars.benchmark.insert.product.Products;
import com.eaybars.benchmark.postgres.insert.ConnectionSupplier;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class ProductsInsertBenchmark {

    @Benchmark
    public void benchmark(Products products, ConnectionSupplier connectionSupplier) throws SQLException {
        Connection connection = connectionSupplier.getConnection();
        JsonObject object = products.getObject();

        String sql = "INSERT INTO product(product_id,description,price,img_url) "
                + "VALUES(?,?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, object.getString("asin"));
            preparedStatement.setString(2, object.getString("description", ""));
            preparedStatement.setDouble(3, Optional
                    .ofNullable(object.getJsonNumber("price"))
                    .map(JsonNumber::doubleValue)
                    .orElse(0.0));
            preparedStatement.setString(4, object.getString("imUrl", ""));
            preparedStatement.executeUpdate();
        }
        connectionSupplier.commit();
    }
}
