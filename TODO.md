# Progressive FastAPI to Spring Boot Migration

## Purpose

Migrate the Olist analytics API from Python/FastAPI to Java/Spring Boot without requiring the existing Plotly frontend to change its successful API contracts.

## Repository References

- Source Python repository (`olist-eda-dashboard`): `/home/kebo/python/olist-migration/olist-eda-dashboard`
- Target Java repository (`olist-eda-dashboard-spring`): `/home/kebo/python/olist-migration/olist-eda-dashboard-spring`
- Source architecture document: `/home/kebo/python/olist-migration/olist-eda-dashboard/docs/backend-architecture.md`
- Source FastAPI application: `/home/kebo/python/olist-migration/olist-eda-dashboard/src/main.py`
- Source database wrappers: `/home/kebo/python/olist-migration/olist-eda-dashboard/src/database.py`
- Source SQL catalog: `/home/kebo/python/olist-migration/olist-eda-dashboard/src/queries.py`
- Source DataFrame utilities: `/home/kebo/python/olist-migration/olist-eda-dashboard/src/utils.py`
- Local source SQLite database: `/home/kebo/python/olist-migration/olist-eda-dashboard/data/olist.sqlite`

Treat the source repository and its frontend submodule as read-only unless the user explicitly requests changes.

## Fixed Decisions

- Java version: 26.
- Build tool: Apache Maven 3.9.16 with Maven Wrapper.
- Framework: stable Spring Boot 4.1.x compatible with Java 26.
- HTTP stack: Spring MVC.
- Data access: Spring JDBC with `NamedParameterJdbcTemplate`; no JPA/Hibernate.
- Database: retain SQLite for the main migration.
- Migration style: preserve successful endpoint paths and JSON contracts, then improve internals.
- Category weights: port the implementation that filters outliers; do not port the unfiltered duplicate handler.
- LOWESS, regression, and forecasting are postponed to Future Goals.

## How To Use This Checklist

- Complete milestones in order unless the user explicitly changes the priority.
- Keep only one milestone actively in progress.
- Inspect the matching Python route, SQL, database wrapper, and utility before porting an endpoint.
- Keep a task unchecked until its tests and the milestone acceptance criteria pass.
- Do not mark an endpoint complete merely because it compiles or returns HTTP 200.
- If the source behavior is ambiguous, record the evidence and obtain a decision instead of inventing behavior.
- Document intentional differences from FastAPI in the target repository.
- Do not start items under Future Goals while the main migration is in progress.

## Main Migration Endpoint Scope

### Direct SQL And Column-Oriented Responses

- `GET /api/orders/daily`
- `GET /api/orders/costs`
- `GET /api/categories/sales`
- `GET /api/sellers/performance`
- `GET /api/sellers/distribution`
- `GET /api/shipping/stages-by-city`
- `GET /api/customers/clv-map`

### Light Formatting And Custom Response Fields

- `GET /api/sellers/review-sales`
- `GET /api/leads/conversion`
- `GET /api/leads/origin`
- `GET /api/reviews/distribution`
- `GET /api/delivery/stages`

### Deterministic Reshaping And Filtering

- `GET /api/orders/hourly`
- `GET /api/sales/monthly`
- `GET /api/categories/monthly-sales`
- `GET /api/sellers/shipping-times`
- `GET /api/categories/weights`

## Milestone 0 — Freeze The Source Baseline

Goal: establish a reproducible source reference before writing the Spring implementation.

### Tasks

- [x] Record the source repository commit hash in `docs/migration-baseline.md`.
- [x] Record the SQLite database filename, size, and SHA-256 hash in `docs/migration-baseline.md` without copying the database into the Java repository.
- [x] Confirm the FastAPI backend starts from the source repository against the canonical database.
- [x] Run every in-scope FastAPI endpoint and record whether it succeeds.
- [x] Create `docs/api-contracts.md` with each in-scope endpoint's path, status, content type, JSON keys, orientation, ordering, date representation, null behavior, and important numeric behavior.
- [x] Capture compact representative JSON fixtures under `src/test/resources/contracts/fastapi/`; avoid committing full-dataset responses when they are excessively large.
- [x] Record full-response counts, array lengths, matrix dimensions, and other invariants for large endpoints.
- [x] Record the observed Sunday-first weekday behavior of `/api/orders/hourly`.
- [x] Record the exact serialized `year_month` representation returned by `/api/sales/monthly`.
- [x] Record category and month ordering for `/api/categories/monthly-sales`.
- [x] Confirm the filtered `/api/categories/weights` handler is the migration contract and capture its top-five category order and filtered array lengths.
- [x] Record the source's successful-response floating-point values at enough precision to define comparison tolerances.
- [x] Mark the five deferred endpoints in `docs/api-contracts.md` and do not treat their absence from Spring as a parity failure.

### Testing Steps

1. Start FastAPI on a dedicated baseline port, such as `8000`.
2. Request every in-scope endpoint directly rather than relying only on generated OpenAPI documentation.
3. Validate that captured JSON parses successfully.
4. Repeat a sample of requests to identify any unstable ordering.
5. If ordering changes between identical requests, document it before designing the Java contract.

### Acceptance Criteria

- [x] The source commit and database hash are recorded.
- [x] All 17 in-scope endpoints have an explicit successful-response contract or a documented source blocker.
- [x] Large-response invariants and representative fixtures are available without committing the full Olist database.
- [x] Date, ordering, label, null, and numeric comparison rules are documented.
- [x] Deferred endpoints are clearly excluded from main-migration parity checks.

## Milestone 1 — Scaffold The Spring Boot Application

Goal: create a minimal Java 26 application that builds, tests, starts, and can reach the configured SQLite database.

### Tasks

- [x] Scaffold a Maven Spring Boot 4.1.x project using Java 26.
- [x] Use `com.olist.dashboard` as the base package unless the user specifies another package before scaffolding.
- [x] Set the Maven artifact name to `olist-eda-dashboard-spring`.
- [x] Add and commit Maven Wrapper files compatible with Maven 3.9.16.
- [x] Add Spring MVC, Spring JDBC, Jackson, SQLite JDBC, and Spring test dependencies.
- [x] Pin the SQLite JDBC driver version explicitly if it is not managed by Spring Boot.
- [x] Establish packages for `config`, `controller`, `service`, `repository`, and `dto` or `response`.
- [x] Add `application.yml` with environment-backed database and CORS configuration.
- [x] Support a configurable database path such as `OLIST_DB_PATH`; do not depend on the process working directory.
- [x] Support comma-separated `CORS_ORIGINS` with the same empty-value behavior documented for FastAPI unless an intentional difference is approved.
- [x] Add a local/parity profile that can use the source database through an environment variable without hardcoding it as the production default.
- [x] Add a context-load test.
- [x] Add a database smoke test that performs a read-only query such as `SELECT 1` and verifies Java 26/SQLite JDBC compatibility.
- [x] Add `.gitignore` rules for Maven output, IDE state, local environment files, generated parity artifacts, and copied database files.

### Testing Steps

1. Run `./mvnw --version` and verify Java 26 and the intended Maven version.
2. Run `./mvnw test`.
3. Start the application with a configured SQLite path.
4. Confirm startup does not create an empty database when the configured file is missing.

### Acceptance Criteria

- [x] `./mvnw test` passes under Java 26.
- [x] The application starts with an explicit database path.
- [x] A missing or unreadable database produces a clear startup or request error and never creates a silent empty replacement.
- [x] No production code depends on an absolute developer-machine path.
- [x] No API migration endpoints have been prematurely implemented.

## Milestone 2 — Build Shared Data Access And API Foundations

Goal: establish reusable infrastructure before porting feature endpoints.

### Tasks

- [x] Configure the SQLite `DataSource` and `NamedParameterJdbcTemplate`.
- [x] Decide and document whether the production connection should be explicitly read-only and whether the pool size needs a SQLite-specific limit.
- [x] Add a consistent mechanism for loading SQL from `src/main/resources/sql/`.
- [x] Define conventions for SQL filenames, row records, row mappers, repositories, services, controllers, and response records.
- [x] Add a repository integration-test fixture with the smallest practical SQLite schema and data needed for deterministic tests.
- [x] Ensure tests never write to the source Olist database.
- [x] Add response types for column-oriented, split/matrix, record-oriented, and chart-specific JSON shapes.
- [x] Configure Jackson deliberately for date values, property names, numeric values, and null serialization.
- [x] Add centralized exception handling that returns an appropriate non-2xx response without exposing raw internal exception text.
- [x] Document the intentional error-response difference from FastAPI handlers that currently return `{"error": ...}` with HTTP 200.
- [x] Add controller-test helpers for exact JSON keys and array dimensions.
- [x] Define a documented floating-point comparison policy for parity tests.
- [x] Design a repeatable parity harness that can compare FastAPI and Spring responses semantically using configurable base URLs.

### Testing Steps

1. Run repository tests against the test SQLite fixture.
2. Test missing SQL resources and malformed SQL error handling.
3. Test JSON serialization for every shared response shape.
4. Test that internal database exceptions are not returned verbatim to clients.

### Acceptance Criteria

- [x] SQL resources can be loaded consistently in production and tests.
- [x] Repository tests use an isolated writable test fixture, never the source database.
- [x] Shared response structures serialize to the documented JSON shapes.
- [x] Error handling and floating-point comparison policies are documented.
- [x] `./mvnw test` passes.

## Milestone 3 — Port Direct SQL Endpoints

Goal: migrate endpoints that primarily execute SQL and return column-oriented data.

### Tasks

- [x] Port `GET /api/orders/daily` with `day` and `order_count` arrays.
- [x] Port `GET /api/orders/costs` with `order_id`, `product_cost`, and `shipping_cost` arrays.
- [x] Port `GET /api/categories/sales` with `category` and `sales` arrays, including the `Other categories` row.
- [x] Port `GET /api/sellers/performance` with `seller_id`, `avg_review_score`, `total_sales`, and `num_orders` arrays.
- [x] Port `GET /api/sellers/distribution` with `bucket` and `seller_count` arrays.
- [x] Port `GET /api/shipping/stages-by-city` with the exact source city and delivery-stage arrays and ordering.
- [x] Port `GET /api/customers/clv-map`, preserving the exact `avg_CLV` property capitalization and geographic arrays.
- [x] For each endpoint, add its SQL resource, row mapping, repository method, service method, controller method, and response record.
- [x] For each endpoint, add repository integration tests and MockMvc contract tests.
- [x] Compare each endpoint with FastAPI against the same full database.

### Testing Steps

1. Run focused repository and controller tests for each endpoint as it is added.
2. Run `./mvnw test` after completing the endpoint group.
3. Run semantic parity comparisons for all seven endpoints.
4. Compare field names and ordering exactly; apply numeric tolerance only to floating-point values.

### Acceptance Criteria

- [x] All seven paths and methods match FastAPI.
- [x] Successful responses have the same content type, JSON keys, array lengths, and ordering as the baseline.
- [x] Integer identifiers/counts remain integers and floating-point fields meet the documented tolerance.
- [x] Repository, controller, and parity tests pass for all seven endpoints.

## Milestone 4 — Port Light Formatting Endpoints

Goal: migrate endpoints that add labels, title-casing, rounding, or chart-specific field names.

### Tasks

- [x] Port `GET /api/sellers/review-sales` with `seller_ids`, `total_sales`, `avg_scores`, and `order_counts`.
- [x] Match pandas two-decimal rounding behavior for seller total sales and average scores; verify tie/half behavior from fixtures rather than assuming a rounding mode.
- [x] Port `GET /api/leads/conversion` with origin cleanup and the four custom arrays.
- [x] Port `GET /api/leads/origin` with source filtering, ordering, and cleaned origin labels.
- [x] Port `GET /api/reviews/distribution` with exact labels such as `1 ★` and the `scores`/`counts` arrays.
- [x] Port `GET /api/delivery/stages` with title-cased cities and approval, carrier, and transit arrays.
- [x] Implement shared text formatting only when it reproduces pandas string behavior for the captured values.
- [x] Add service unit tests for title-casing, underscore replacement, star labels, rounding, empty results, and null handling.
- [x] Add repository integration tests and MockMvc contract tests for all five endpoints.
- [x] Compare all five endpoints with FastAPI against the same full database.

### Testing Steps

1. Run transformation unit tests independently of Spring.
2. Run repository tests against the SQLite fixture.
3. Run MockMvc contract tests.
4. Run full-database parity comparisons and inspect labels and ordering manually once.

### Acceptance Criteria

- [x] All five successful response shapes match the baseline exactly.
- [x] Label cleanup, title-casing, and star formatting match FastAPI.
- [x] Rounded values meet the baseline's exact or documented tolerant comparison.
- [x] Unit, repository, controller, and parity tests pass.

## Milestone 5 — Port Deterministic Reshaping And Filtering

Goal: reproduce pandas pivots, matrices, date shaping, and outlier filters in testable Java services.

### 5A — Hourly Orders Matrix

- [x] Port `GET /api/orders/hourly`.
- [x] Preserve the split response with `index`, `columns`, and `data`.
- [x] Preserve 24 hour columns and the observed source column types.
- [x] Preserve the source weekday order, including SQLite Sunday value `0` behavior.
- [x] Add matrix dimension, zero-count, weekday-order, and JSON contract tests.

### 5B — Monthly Sales

- [x] Port `GET /api/sales/monthly` for the five fixed translated categories.
- [x] Preserve the exact baseline `year_month` serialization rather than choosing a cleaner format during migration.
- [x] Preserve category property names, null values, and month ordering.
- [x] Add date serialization and missing-category-value tests.

### 5C — Monthly Category Sales Pivot

- [x] Port `GET /api/categories/monthly-sales`.
- [x] Determine the top five raw categories by total delivered sales.
- [x] Build the split response with months as `index`, categories as `columns`, and sales as `data`.
- [x] Preserve observed category and month ordering.
- [x] Fill missing category/month cells with numeric zero.
- [~] Add tests for top-five selection, ties, missing cells, ordering, and matrix dimensions.

### 5D — Seller Shipping IQR Filter

- [x] Port `GET /api/sellers/shipping-times`.
- [x] Preserve seller volume buckets from the source SQL.
- [x] Calculate Q1, Q3, and IQR independently per bucket.
- [x] Reproduce pandas' default quantile interpolation behavior.
- [x] Retain values within the inclusive interval `Q1 - 1.5 * IQR` through `Q3 + 1.5 * IQR`.
- [x] Preserve source row/group ordering as defined by the baseline contract.
- [~] Add tests for each bucket, small groups, repeated values, exact boundaries, and upper/lower outliers.

### 5E — Filtered Category Weights

- [x] Port `GET /api/categories/weights` using the filtered Python implementation selected by the user.
- [x] Query non-null raw product category names and weights with the source join behavior.
- [x] Select the five categories with the greatest occurrence counts.
- [x] Preserve repeated weight observations produced by `order_items`.
- [x] Compute the mean and sample standard deviation (`ddof=1`) independently per category.
- [x] Retain values within the inclusive interval `mean - 0.8 * stddev` through `mean + 0.8 * stddev`.
- [x] Retain a one-value group when sample standard deviation cannot be computed, matching effective pandas behavior.
- [x] Preserve the baseline category key order and resolve ties according to observed FastAPI output.
- [~] Add tests for top-five selection, ties, repeated values, one-value groups, exact boundaries, and outliers on both sides.

### Testing Steps

1. Test every transformation as a plain Java service without the web layer.
2. Use hand-calculated fixtures for sample standard deviation and IQR boundaries.
3. Run repository and MockMvc tests for all five endpoints.
4. Run full-database parity comparisons.
5. For matrix and filtered responses, compare dimensions and every retained value, not only summary statistics.

### Acceptance Criteria

- [ ] All five endpoints reproduce their baseline JSON shape and ordering.
- [ ] Matrix and pivot dimensions match exactly.
- [ ] Date serialization matches the captured FastAPI behavior.
- [ ] IQR and category-weight retained sets match FastAPI for the full dataset.
- [ ] Transformation, repository, controller, and parity tests pass.
- [ ] `./mvnw test` passes.

## Milestone 6 — Full Verification, Packaging, And Cutover Readiness

Goal: prove that the Spring backend can replace FastAPI for the agreed main scope.

### Tasks

- [ ] Run all 17 in-scope endpoints in FastAPI and Spring against the same database.
- [ ] Run the complete semantic parity suite and store a concise report under `docs/`.
- [ ] Verify CORS with an allowed frontend origin and with an unconfigured origin.
- [ ] Smoke-test every affected Plotly chart against Spring without redesigning the frontend.
- [ ] Confirm deferred endpoints are documented and are not represented as completed Spring features.
- [ ] Add a production-ready executable JAR build.
- [ ] Add a container build that uses an externally supplied database path; do not bake the local database or download it during image construction without approval.
- [ ] Add a target-repository `README.md` covering prerequisites, configuration, local startup, tests, parity checks, JAR execution, and container execution.
- [ ] Document all approved differences from the Python backend.
- [ ] Verify secrets, databases, local fixtures, and generated comparison output are not accidentally tracked.
- [ ] Keep the Python backend available until the user approves final cutover.

### Testing Steps

1. Run `./mvnw clean verify`.
2. Run the full parity suite with both backends on separate ports.
3. Start the packaged JAR under Java 26 and repeat representative API checks.
4. Build and start the container with a mounted/configured SQLite database.
5. Perform a frontend smoke test covering every in-scope chart.

### Final Acceptance Criteria

- [ ] All 17 in-scope endpoints pass their milestone tests.
- [ ] Full-database responses match the documented contracts and numeric tolerances.
- [ ] The existing frontend can consume the Spring responses without response-shape changes.
- [ ] CORS works through externalized configuration.
- [ ] The JAR and container start reliably with an explicitly configured SQLite file.
- [ ] No source Python or frontend files were changed as part of the migration.
- [ ] Deferred work is accurately listed under Future Goals.
- [ ] The user has reviewed the parity report and approved cutover readiness.

## Non-Goals And Restraints

The following are explicitly outside the main migration:

- `GET /api/delivery/trend` and LOWESS smoothing.
- `GET /api/sales/regression`.
- `GET /api/sales/forecast`.
- Regression or forecast demonstrations based on `numpy.polyfit` or `numpy.poly1d`.
- `GET /api/shipping/daily-average` until the missing source query and intended contract are defined.
- `GET /api/customers/rfm` until the missing source query and intended contract are defined.
- Porting unused `utils.view_table` or unused/broken SQL solely for completeness.
- Replacing SQLite, changing the database schema, or migrating the data.
- JPA/Hibernate entity modeling.
- Renaming endpoints or redesigning successful JSON contracts.
- Translating raw category names unless the source contract already translates them.
- Frontend or Plotly redesign.
- Authentication, authorization, caching, native-image compilation, or unrelated infrastructure expansion.
- Performance optimization before contract parity.
- Placeholder controllers for deferred endpoints that could be mistaken for completed features.
- Removing the Python backend before user-approved cutover.

## Future Goals

These items require separate planning and approval after the main migration:

- [ ] Decide whether statistical demonstrations belong in Spring, remain in Python, or run as a separate analytics service.
- [ ] Reimplement the regression demonstration originally planned around `numpy.polyfit` and `numpy.poly1d`, with documented Java/Python numerical parity expectations.
- [ ] Reimplement the sales forecast demonstration and define an accepted forecasting method and validation dataset.
- [ ] Evaluate a Java LOWESS/LOESS implementation for `GET /api/delivery/trend` and define acceptable numeric tolerance against `statsmodels.lowess`.
- [ ] Define and implement the missing `daily_avg_shipping_time` source behavior before migrating `/api/shipping/daily-average`.
- [ ] Define and implement the missing `rfm_buckets` source behavior before migrating `/api/customers/rfm`.
- [ ] Revisit whether raw category names should be translated through a versioned API change.
- [ ] Add OpenAPI descriptions and formal published response schemas after parity is stable.
- [ ] Add Actuator health/metrics and broader observability.
- [ ] Evaluate safe caching for expensive read-only analytics queries.
- [ ] Add performance benchmarks and optimize SQL only after preserving contract tests.
- [ ] Evaluate PostgreSQL or another server database if SQLite concurrency or deployment becomes limiting.
- [ ] Evaluate authentication and authorization if the API becomes non-public or accepts protected data.
- [ ] Retire the Python backend only after full feature parity, deployment validation, and explicit user approval.

