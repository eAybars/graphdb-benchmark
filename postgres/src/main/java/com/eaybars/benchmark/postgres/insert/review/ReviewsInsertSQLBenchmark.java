package com.eaybars.benchmark.postgres.insert.review;

import com.eaybars.benchmark.insert.review.Reviews;
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
public class ReviewsInsertSQLBenchmark {

    @Benchmark
    public void benchmark(Reviews reviews, ConnectionSupplier connectionSupplier) throws SQLException {
        Connection connection = connectionSupplier.getConnection();
        JsonObject object = reviews.getObject();

        String sqlProductSelect = "select product_id from product where product_id=?)";
        String sqlPersonSelect = "select reviewer_id from person where reviewer_id=?)";

        String sqlPersonInsert = "INSERT INTO person(reviewer_id,reviewer_name) "
                + "VALUES(?,?)";

        String sqlProductInsert = "INSERT INTO product(product_id) "
                + "VALUES(?)";

        String sqlReviewInsert = "INSERT INTO review(product_id,reviewer_id,summary,review_text,overall,unixReviewTime) "
                + "VALUES(?,?,?,?,?,?)";

        try (PreparedStatement productSelectStatement = connection.prepareStatement(sqlProductSelect);
             PreparedStatement personSelectStatement = connection.prepareStatement(sqlPersonSelect);
             PreparedStatement personInsertStatement = connection.prepareStatement(sqlPersonInsert);
             PreparedStatement productInsertStatement = connection.prepareStatement(sqlProductInsert);
             PreparedStatement reviewInsertStatement = connection.prepareStatement(sqlReviewInsert);) {

            personSelectStatement.setString(1, object.getString("reviewerID"));

            if (!personSelectStatement.executeQuery().next()) {
                personInsertStatement.setString(1, object.getString("reviewerID"));
                personInsertStatement.setString(2, object.getString("reviewerName", "anonymous"));
                personInsertStatement.executeUpdate();
            }

            productSelectStatement.setString(1, object.getString("asin"));
            if (!productSelectStatement.executeQuery().next()) {
                productInsertStatement.setString(1, object.getString("asin"));
                productInsertStatement.executeUpdate();
            }

            reviewInsertStatement.setString(1, object.getString("asin"));
            reviewInsertStatement.setString(2, object.getString("reviewerID"));
            reviewInsertStatement.setString(3, object.getString("summary"));
            reviewInsertStatement.setString(4, object.getString("reviewText"));
            reviewInsertStatement.setDouble(5, object.getJsonNumber("overall").doubleValue());
            reviewInsertStatement.setLong(6, object.getJsonNumber("unixReviewTime").longValue());
            reviewInsertStatement.executeUpdate();

        }

        connectionSupplier.commit();

    }
}
