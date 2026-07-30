# Olist EDA Dashboard Spring API

Read-only Spring Boot replacement for the FastAPI analytics API used by the Olist Plotly dashboard.
It preserves the 17 agreed successful-response contracts while retaining SQLite as an externally
supplied, read-only analytics database.

## Prerequisites

- Java 26
- Maven Wrapper included in this repository (Maven 3.9.16)
- An absolute path to a readable Olist SQLite database
- Docker or another OCI-compatible runtime only when running the container workflow

The application never supplies a database default. `OLIST_DB_PATH` must be an absolute path to an
existing, readable SQLite file; the JDBC connection opens it with SQLite `mode=ro`.

## Configuration

| Variable | Required | Meaning |
| --- | --- | --- |
| `OLIST_DB_PATH` | Yes | Absolute path to the externally supplied SQLite file. |
| `CORS_ORIGINS` | No | Comma-separated allowed browser origins, such as `http://127.0.0.1:5173`. Empty means no cross-origin browser origins are allowed, matching FastAPI. |

Origins must match the browser origin exactly: `http://localhost:5173` and
`http://127.0.0.1:5173` are different origins.

## Local startup

```bash
OLIST_DB_PATH=/absolute/path/to/olist.sqlite \
CORS_ORIGINS=http://127.0.0.1:5173 \
./mvnw spring-boot:run
```

Verify a representative endpoint:

```bash
curl --fail --header 'Accept: application/json' \
  http://127.0.0.1:8080/api/orders/daily
```

## Tests and full parity

Run the isolated test suite:

```bash
./mvnw clean verify
```

For full-database parity, start FastAPI and Spring on separate ports against the same canonical
database, then run the opt-in harness. When `PARITY_ENDPOINTS` is omitted, it checks all 17
main-scope endpoints.

```bash
PARITY_FASTAPI_BASE_URL=http://127.0.0.1:18000 \
PARITY_SPRING_BASE_URL=http://127.0.0.1:18085 \
./mvnw -Dparity.enabled=true -Dtest=ConfiguredParityHarnessTest test
```

See [the parity harness guide](docs/parity-harness.md) and
[the Milestone 6 verification report](docs/milestone-6-cutover-readiness.md) for the comparison
rules, results, documented differences, and deferred endpoints.

## Executable JAR

Build the Spring Boot executable JAR:

```bash
./mvnw clean package
```

Then run it with explicit configuration:

```bash
OLIST_DB_PATH=/absolute/path/to/olist.sqlite \
CORS_ORIGINS=http://127.0.0.1:5173 \
java -jar target/olist-eda-dashboard-spring-0.0.1-SNAPSHOT.jar
```

## Container

The `Dockerfile` intentionally expects the already-built JAR and does not copy or download a
database. Build it after `./mvnw clean package`:

```bash
docker build --tag olist-eda-dashboard-spring:local .
```

Run it with a read-only database mount. Ensure the mounted file is readable by the container's
non-root UID `10001`:

```bash
docker run --rm --publish 8080:8080 \
  --volume /absolute/path/to/olist.sqlite:/data/olist.sqlite:ro \
  --env OLIST_DB_PATH=/data/olist.sqlite \
  --env CORS_ORIGINS=http://127.0.0.1:5173 \
  olist-eda-dashboard-spring:local
```

On SELinux hosts, append `,Z` to the mount option if required by the local Docker/Podman setup.
The application rejects missing, relative, unreadable, or non-regular database paths rather than
creating an empty SQLite file.

## Scope and frontend compatibility

The migrated main scope contains 17 successful API endpoints. LOWESS delivery trend, regression,
forecasting, daily shipping average, and RFM remain deferred; they are not placeholder Spring
features. The source Python backend remains available until the user explicitly approves cutover.

The source Vite frontend can target this API without source changes by setting
`VITE_API_BASE_URL` when starting its development server and configuring the matching origin in
`CORS_ORIGINS`. Its known `bucket`/`buckets` seller-distribution defect is documented in the
Milestone 6 report and is intentionally not corrected here because the approved FastAPI contract
uses singular `bucket`.
