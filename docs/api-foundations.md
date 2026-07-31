# Shared API Foundations

This document defines the shared infrastructure that feature migrations must use. It applies to
successful-response parity work and does not introduce any API endpoint by itself.

## SQLite access policy

Production and parity-profile database paths are supplied only through `OLIST_DB_PATH` and
`OLIST_PARITY_DB_PATH`. `OlistDatabaseProperties` rejects blank, relative, missing, non-regular,
and unreadable paths before JDBC can open them.

`SqliteJdbcConfiguration` creates one explicit `SQLiteDataSource` using a URI with `mode=ro`, and
exposes a `NamedParameterJdbcTemplate`. The read-only URI prevents SQLite from silently creating
an empty database and prevents repository writes. The application deliberately does **not** add a
connection pool yet: SQLite is a file-backed read-only analytics source, and pool sizing/concurrent
reader behavior has not been measured. `SQLiteDataSource` therefore opens connections on demand.
Revisit this decision only with workload evidence and an explicit SQLite concurrency test.

`spring.sql.init.mode=never` also prevents Spring Boot from attempting schema or data initialization
against the externally supplied analytics file.

Tests seed disposable SQLite files under the system temporary directory before Spring starts. The
production data source then opens those test files in read-only mode. Tests never write to the
canonical source database.

## Package and repository conventions

- `config`: environment properties, JDBC, CORS, and Jackson configuration.
- `repository`: SQL resource loading, named-parameter query execution, endpoint-specific
  repositories, and typed row mappers.
- `service`: deterministic data shaping after repository mapping; no HTTP concerns.
- `controller`: route/status/content-type handling only; do not create a controller until its
  endpoint milestone begins.
- `dto`: public response records. Preserve source property spelling with `@JsonProperty` whenever
  a captured name is not lower camel case, for example `avg_CLV`.

Each future repository exposes domain-specific methods, loads one SQL resource through
`SqlResourceLoader`, supplies named parameters as a map, and maps rows directly to a typed record.
`SqlQueryExecutor` centralizes the named JDBC call and translates driver failures to
`AnalyticsDataAccessException`. See [SQL resource conventions](sql-resource-conventions.md) for
the required file naming and classpath rules.

## JSON policy

The application configures Jackson explicitly to preserve rather than normalize source contracts:

- `LocalDate` and `LocalDateTime` serialize as ISO strings, never epoch timestamps.
- Unannotated properties use lower camel case; source-specific spelling is opt-in per property.
- JSON numbers remain numbers. No global rounding, stringification, or decimal-scale normalization
  is applied; `BigDecimal` writes as a plain JSON number.
- Null properties and collection elements are retained. The frozen canonical responses happen to
  contain no nulls, but that observation is not a global suppression rule.
- `ColumnarResponse`, `SplitMatrixResponse`, `RecordOrientedResponse`, and `ChartResponse`
  serialize to the root shapes used by FastAPI rather than Java wrapper envelopes.

`JsonContractAssertions` provides future MockMvc tests with exact object-key order,
parallel-array-length, and split-matrix-dimension assertions.

## Error policy and intentional FastAPI difference

The source has mixed failures: several handlers catch exceptions and return raw
`{"error": "<exception text>"}` with HTTP 200, while uncaught failures take FastAPI's normal
server-error path. Its database wrapper can also call `sys.exit(1)` when opening a connection
fails.

Spring intentionally standardizes these failures. `ApiExceptionHandler` returns JSON with a
sanitized `error` field and a non-2xx status:

| Failure | HTTP status | Client message |
| --- | ---: | --- |
| Database cannot be opened | 503 | `The analytics database is unavailable.` |
| Repository/query failure | 500 | `The analytics query could not be completed.` |
| Unexpected server failure | 500 | `Internal server error.` |

SQL, filesystem paths, driver messages, and exception messages are never returned to clients.
This is an intentional error-response divergence and is outside successful-response parity.

## Floating-point policy

For successful parity comparisons, require exact field names and order, labels, dates, array order,
matrix dimensions, integer values, and zero-versus-null behavior. Require ordinary floating values
to satisfy:

`abs(actual - expected) <= max(1e-9, 1e-12 * max(1, abs(expected)))`.

Do not apply that tolerance to integers, strings, nulls, or non-finite values. Preserve documented
endpoint-specific behavior such as pandas two-decimal rounding and the monthly category pivot's
10-decimal serialization. The authoritative endpoint details remain in
[the frozen API contracts](api-contracts.md).
