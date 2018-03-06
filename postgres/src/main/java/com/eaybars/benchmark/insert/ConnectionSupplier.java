package com.eaybars.benchmark.insert;

import com.eaybars.benchmark.Transaction;
import org.openjdk.jmh.annotations.*;

import java.sql.*;

@State(Scope.Thread)
public class ConnectionSupplier {
    private Connection connection;
    private Transaction.Options options;
    private int count;
    private int lastCommit;


    @Setup(Level.Trial)
    public void setUp() throws Exception {
        options = Transaction.currentOptions();
        initConnection();
    }

    private void initConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection("jdbc:postgresql://postgres:5432/testdb", "root", "123456");
        connection.setAutoCommit(false);

        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet table = dbm.getTables(null, null, "product", null);
        if (!table.next()) {
            // creates product table and indexes
            String sqlCreateProductStatement = "CREATE TABLE product(" +
                    "product_id VARCHAR(100) PRIMARY KEY, " +
                    "description text, " +
                    "price NUMERIC, " +
                    "img_url text )";
            executeStatement(sqlCreateProductStatement);
            executeStatement("CREATE INDEX price_field_index ON product (price)");

            // creates also_viewed table and indexes
            String sqlCreateAlsoViewedStatement = "CREATE TABLE also_viewed(" +
                    "product_id  VARCHAR(100) REFERENCES product(product_id), " +
                    "also_viewed_id  VARCHAR(100) REFERENCES product(product_id), " +
                    "PRIMARY KEY (product_id, also_viewed_id))";
            executeStatement(sqlCreateAlsoViewedStatement);
            executeStatement("CREATE INDEX product_id_index ON also_viewed (product_id)");
            executeStatement("CREATE INDEX also_viewed_id_index ON also_viewed (also_viewed_id)");

            // creates buy_after_viewing table and indexes
            String sqlCreateBuyAfterViewingStatement = "CREATE TABLE buy_after_viewing(" +
                    "product_id  VARCHAR(100) REFERENCES product(product_id), " +
                    "buy_after_viewing_id  VARCHAR(100) REFERENCES product(product_id), " +
                    "PRIMARY KEY (product_id, buy_after_viewing_id))";
            executeStatement(sqlCreateBuyAfterViewingStatement);
            executeStatement("CREATE INDEX product_id_index2 ON buy_after_viewing (product_id)");
            executeStatement("CREATE INDEX buy_after_viewing_id_index ON buy_after_viewing (buy_after_viewing_id)");

            // creates category table and indexes
            String sqlCreateCategory = "CREATE TABLE category(" +
                    "category_name VARCHAR(100) PRIMARY KEY)";
            executeStatement(sqlCreateCategory);

            // creates product_category table and indexes
            String sqlCreateProductCategoryStatement = "CREATE TABLE product_category(" +
                    "product_id  VARCHAR(100) REFERENCES product(product_id) , " +
                    "category_name  VARCHAR(100) REFERENCES category(category_name) , " +
                    "PRIMARY KEY (product_id, category_name))";
            executeStatement(sqlCreateProductCategoryStatement);
            executeStatement("CREATE INDEX product_id_index3 ON product_category (product_id)");
            executeStatement("CREATE INDEX category_name_index ON product_category (category_name)");

            // create person table and indexes
            String sqlCreatePersonStatement = "CREATE TABLE person(" +
                    "reviewer_id VARCHAR(100) PRIMARY KEY, " +
                    "reviewer_name VARCHAR(100))";
            executeStatement(sqlCreatePersonStatement);

            // creates review table and indexes
            String sqlReviewStatement = "CREATE TABLE review(" +
                    "review_id  SERIAL PRIMARY KEY, " +
                    "product_id  VARCHAR(100) REFERENCES product(product_id), " +
                    "reviewer_id  VARCHAR(100) REFERENCES person(reviewer_id), " +
                    "summary TEXT, " +
                    "review_text TEXT, " +
                    "overall NUMERIC, " +
                    "unixReviewTime BIGINT)";
            executeStatement(sqlReviewStatement);
            executeStatement("CREATE INDEX reviewer_id_index ON review (reviewer_id)");
            executeStatement("CREATE INDEX product_id_index4 ON review (product_id)");
            executeStatement("CREATE INDEX overall_field_index ON review (overall)");
            executeStatement("CREATE INDEX unix_review_time_index ON review (unixReviewTime)");

        }
        table.close();
    }

    private void executeStatement(String sql) throws SQLException {
        PreparedStatement createProductStatement = connection.prepareStatement(sql);
        createProductStatement.executeUpdate();
        createProductStatement.close();
    }

    @Setup(Level.Invocation)
    public void next() throws Exception {
        if (options.getGraphReinitialisationPeriod() > 0 &&
                count > 0 && count % options.getGraphReinitialisationPeriod() == 0) {
            tearDown();
            initConnection();
        } else {
            commit();
        }
        count++;
    }

    public int getCount() {
        return count;
    }


    public void commit() {
        if (options.getCommitInterval() > 0 && count > 0 && lastCommit < count &&
                (options.getCommitInterval() == 1 || count % options.getCommitInterval() == 0)) {
            try {
                connection.commit();
                lastCommit = count;
            } catch (Exception e) {
            }
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        try {
            if (options.getCommitInterval() > 0 && lastCommit < count) {
                connection.commit();
                lastCommit = count;
            }
        } catch (Exception e) {
        } finally {
            connection.close();
        }
    }


    public Connection getConnection() {
        return connection;
    }
}
