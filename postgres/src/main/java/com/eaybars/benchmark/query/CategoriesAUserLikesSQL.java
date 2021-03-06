package com.eaybars.benchmark.query;

import com.eaybars.benchmark.Information;
import com.eaybars.benchmark.ConnectionSupplier;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.eaybars.benchmark.Arguments.ARGUMENTS;

/**
 * A simple linear traversal
 */
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class CategoriesAUserLikesSQL {

    private String id = ARGUMENTS.extract("-query.caul.user=", Function.identity(), "A2HEQM6A1GGSAE");
    private static String SQL = "select distinct c.category_name FROM category c " +
            "INNER JOIN product_category pc ON pc.category_name = c.category_name " +
            "INNER JOIN product p ON p.product_id = pc.product_id " +
            "INNER JOIN review r ON r.product_id = p.product_id AND r.overall > 3 " +
            "INNER JOIN person pe ON pe.reviewer_id = r.reviewer_id " +
            "WHERE pe.reviewer_id = ?";


    @Benchmark
    public List<String> query(ConnectionSupplier connectionSupplier) throws Exception {
        Connection connection = connectionSupplier.getConnection();

        List<String> result = new LinkedList<>();

        try (PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setString(1, id);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
        }
        try {
            Information.BENCHMARK_RESULT.put("CategoriesAUserLikes", result.size());
        } catch (IOException e) {
        }
        return result;
    }

    @TearDown
    public void saveResults() throws IOException {
        Information.BENCHMARK_RESULT.flush();
    }

}
