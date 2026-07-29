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

## Scope and frontend compatibility

The migrated main scope contains 17 successful API endpoints. LOWESS delivery trend, regression, forecasting, daily shipping average, and RFM remain deferred; they are not placeholder Spring features. 

The source Python backend remains available until the user explicitly approves cutover.
