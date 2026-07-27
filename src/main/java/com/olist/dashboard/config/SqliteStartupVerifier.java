import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.olist.dashboard.repository.SqlResourceLoader;

/** Fails startup clearly unless the configured SQLite database can be queried read-only. */
@Component
public class SqliteStartupVerifier implements ApplicationRunner {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SqlResourceLoader sqlResourceLoader;

    public SqliteStartupVerifier(
            NamedParameterJdbcTemplate jdbcTemplate,
            SqlResourceLoader sqlResourceLoader) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlResourceLoader = sqlResourceLoader;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            Integer result = jdbcTemplate.queryForObject(
                    sqlResourceLoader.load("shared/select-one.sql"), Map.of(), Integer.class);
            if (!Integer.valueOf(1).equals(result)) {
                throw new IllegalStateException("SQLite read-only smoke query did not return 1");
            }
        } catch (DataAccessException exception) {
            throw new IllegalStateException(
                    "Unable to open OLIST_DB_PATH as a readable SQLite database in read-only mode", exception);
        }
    }
}
