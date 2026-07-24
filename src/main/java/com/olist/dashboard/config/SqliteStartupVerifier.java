import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Fails startup clearly unless the configured SQLite database can be queried read-only. */
@Component
public class SqliteStartupVerifier implements ApplicationRunner {

    private final OlistDatabaseProperties databaseProperties;

    public SqliteStartupVerifier(OlistDatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        String jdbcUrl = databaseProperties.readOnlyJdbcUrl();
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT 1")) {
            if (!resultSet.next() || resultSet.getInt(1) != 1) {
                throw new IllegalStateException("SQLite read-only smoke query did not return 1");
            }
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to open OLIST_DB_PATH as a readable SQLite database in read-only mode", exception);
        }
    }
}
