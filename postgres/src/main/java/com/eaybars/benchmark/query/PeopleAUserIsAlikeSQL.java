package com.eaybars.benchmark.query;

import com.eaybars.benchmark.ConnectionSupplier;
import com.eaybars.benchmark.Information;
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
public class PeopleAUserIsAlikeSQL {

    private String id = ARGUMENTS.extract("-query.caul.user=", Function.identity(), "A2HEQM6A1GGSAE");

    private static String SQL = "select u.reviewer_id, COUNT (myProducts.pid) " +
            "from (select p.product_id as pid FROM product p " +
                "INNER JOIN review r ON r.product_id = p.product_id " +
                "INNER JOIN person pe ON pe.reviewer_id = r.reviewer_id " +
                "WHERE pe.reviewer_id = ?) as myProducts " +
            "INNER JOIN review rw ON rw.product_id = myProducts.pid " +
            "INNER JOIN person u ON u.reviewer_id = rw.reviewer_id AND u.reviewer_id <> ? " +
            "GROUP BY u.reviewer_id " +
            "ORDER BY COUNT (myProducts.pid) DESC";


    @Benchmark
    public List<String> query(ConnectionSupplier connectionSupplier) throws Exception {
        Connection connection = connectionSupplier.getConnection();

        List<String> result = new LinkedList<>();

        try (PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setString(1, id);
            ps.setString(2, id);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
        }
        try {
            Information.BENCHMARK_RESULT.put("PeopleAUserIsAlikeSQL", result.size());
        } catch (IOException e) {
        }
        return result;
    }

    @TearDown
    public void saveResults() throws IOException {
        Information.BENCHMARK_RESULT.flush();
    }

}
