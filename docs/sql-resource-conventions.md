# SQL Resource Conventions

SQL is stored as UTF-8 classpath resources below `src/main/resources/sql/`. Repositories load it
through `SqlResourceLoader` with a path relative to that directory, never through a filesystem path
or the process working directory.

Use this naming convention:

- `sql/<area>/<query-name>.sql`, for example `sql/orders/daily.sql` or
  `sql/shipping/stages-by-city.sql`.
- Directory and filename segments use lower kebab case (`[a-z0-9]+` words separated by single
  hyphens). The filename ends in `.sql`.
- A resource contains one logical query. It may use CTEs and named JDBC parameters such as
  `:startDate`, but must not build SQL by string concatenation in Java.
- Alias selected columns to the names expected by the associated row record and row mapper. Keep
  source-query ordering explicit with `ORDER BY` rather than relying on SQLite's incidental order.

`sql/shared/select-one.sql` is the small non-endpoint resource used to verify that production SQL
is packaged and loaded from that root. It is not an API query.

Tests use the same loader and classpath mechanism. Isolated test-only SQL belongs below
`src/test/resources/sql/test-fixtures/` and must use a distinct path rather than shadowing a
production resource. `SqlResourceLoader` intentionally returns SQL unchanged; malformed SQL is
therefore exercised by repository integration tests against the isolated SQLite test fixture, not
by trying to parse SQL in the loader.
