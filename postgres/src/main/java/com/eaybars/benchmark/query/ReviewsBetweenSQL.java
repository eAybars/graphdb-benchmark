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

import static com.eaybars.benchmark.Arguments.ARGUMENTS;

/**
 * A simple linear traversal
 */
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class ReviewsBetweenSQL {

    //six months before 1406073600L most recent review unix time
    private long start = ARGUMENTS.extract("-query.rb.start=", Long::parseLong, 1390521600L);
    private long end = ARGUMENTS.extract("-query.rb.end=", Long::parseLong, 1393113600L);

    private static String SQL = "select * FROM review r where r.unixReviewTime BETWEEN ? and ?";


    @Benchmark
    public List<String> query(ConnectionSupplier connectionSupplier) throws Exception {
        Connection connection = connectionSupplier.getConnection();

        List<String> result = new LinkedList<>();

        try (PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setLong(1, start);
            ps.setLong(2, end);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
        }
        try {
            Information.BENCHMARK_RESULT.put("ReviewsBetweenSQL", result.size());
        } catch (IOException e) {
        }
        return result;
    }

    @TearDown
    public void saveResults() throws IOException {
        Information.BENCHMARK_RESULT.flush();
    }

}
