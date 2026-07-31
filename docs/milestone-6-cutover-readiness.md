# Milestone 6 — Full verification and cutover readiness

Verified on 2026-07-30 against the canonical read-only source SQLite database recorded in
[migration-baseline.md](migration-baseline.md). This report consolidates main-scope verification
evidence; it does not claim that deferred statistical endpoints have been migrated.

## Full 17-endpoint semantic parity

FastAPI and Spring Boot ran on separate local ports against the same canonical database. With
PARITY_ENDPOINTS omitted, the harness selected all 17 frozen successful endpoints. It requires
HTTP 200, exact application/json, complete parsed JSON key/order/shape/type equality, and the
documented floating-point tolerance.

~~~
PARITY_FASTAPI_BASE_URL=http://127.0.0.1:18000 \
PARITY_SPRING_BASE_URL=http://127.0.0.1:18085 \
MAVEN_USER_HOME=/tmp/olist-m6-maven-home \
./mvnw -Dmaven.repo.local=/tmp/olist-m3-m2 \
  -Dparity.enabled=true -Dtest=ConfiguredParityHarnessTest test
~~~

Result: one passing test, zero failures, zero errors, zero skips, and zero endpoint mismatches.
Temporary FastAPI and Spring processes were stopped afterward.

| Endpoint | Result |
| --- | --- |
| GET /api/orders/daily | Matched |
| GET /api/orders/costs | Matched |
| GET /api/categories/sales | Matched |
| GET /api/sellers/performance | Matched |
| GET /api/sellers/distribution | Matched |
| GET /api/shipping/stages-by-city | Matched |
| GET /api/customers/clv-map | Matched |
| GET /api/sellers/review-sales | Matched |
| GET /api/leads/conversion | Matched |
| GET /api/leads/origin | Matched |
| GET /api/reviews/distribution | Matched |
| GET /api/delivery/stages | Matched |
| GET /api/orders/hourly | Matched |
| GET /api/sales/monthly | Matched |
| GET /api/categories/monthly-sales | Matched |
| GET /api/sellers/shipping-times | Matched |
| GET /api/categories/weights | Matched — approved filtered implementation |

The frozen response invariants, fixtures, shapes, dates, ordering, labels, and numeric policy are
in [api-contracts.md](api-contracts.md). The opt-in harness deliberately excludes the five
deferred endpoints below.

## Automated project verification and executable JAR

~~~
MAVEN_USER_HOME=/tmp/olist-m6-maven-home \
./mvnw -Dmaven.repo.local=/tmp/olist-m3-m2 clean verify
~~~

Result: 79 tests run, 78 passing, zero failures, zero errors, and one skipped opt-in live-parity
test. The build produced target/olist-eda-dashboard-spring-0.0.1-SNAPSHOT.jar.

It was then started under Java 26 with an explicit canonical database path, a configured browser
origin, and a dedicated port:

~~~
OLIST_DB_PATH=/home/kebo/python/olist-migration/olist-eda-dashboard/data/olist.sqlite \
CORS_ORIGINS=http://127.0.0.1:5173 \
java -jar target/olist-eda-dashboard-spring-0.0.1-SNAPSHOT.jar --server.port=18086
~~~

The startup verifier completed its read-only SQLite SELECT 1, and GET /api/orders/daily returned
200 application/json with 634 days. The JAR process was stopped after verification.

## CORS verification

OlistCorsIntegrationTests starts a real Spring HTTP server using an isolated temporary SQLite
fixture. It verifies both sides of the browser contract:

| Request | Result |
| --- | --- |
| Preflight from configured http://localhost:5173 | 200, matching Access-Control-Allow-Origin, GET, and credentials headers |
| Preflight from https://unconfigured.example | 403 and no Access-Control-Allow-Origin header |

The packaged JAR was also checked with http://127.0.0.1:5173: an allowed preflight returned 200
with the expected grant headers, while the unconfigured-origin preflight returned 403. Origins are
deliberately exact; localhost and 127.0.0.1 must each be configured when both are used.

## Frontend compatibility audit and required browser smoke

The source frontend was treated as read-only. A static audit confirms all API hooks use
VITE_API_BASE_URL, so no frontend response-shape change is required to target Spring. Fourteen
current Plotly chart consumers map to verified endpoints:

- Orders: daily orders, hourly heatmap, customer CLV map, order-cost histograms.
- Categories: category treemap, monthly category sales, category-weight box plot.
- Delivery: seller shipping-time box plot and delivery stages.
- Sellers: lead origins, lead conversion, seller distribution, review distribution, and
  review-sales scatter.

No current source chart consumes /api/sellers/performance, /api/shipping/stages-by-city, or
/api/sales/monthly; they remain full-parity-verified API endpoints. The Delivery tab also has a
separate deferred LOWESS trend request, which is expected to remain unavailable during this
migration.

Browser-level chart rendering could not be run here: Node.js/npm are unavailable, the source
frontend has no installed node_modules, and no Docker/Podman runtime is installed. The source
checkout and frontend were not altered to work around that limitation. On a host with Node 20+ and
the source frontend dependencies, run the following in a temporary copy of the source frontend:

~~~
VITE_API_BASE_URL=http://127.0.0.1:18085 \
npm run dev -- --host 127.0.0.1 --port 5173
~~~

Start Spring with the same database and CORS_ORIGINS=http://127.0.0.1:5173, then smoke the Orders,
Categories, Delivery, and Sellers tabs. Verify the fourteen requests above render their charts; the
deferred Delivery Trend card is the only expected API failure on those tabs. Do not include the
Predictions tab in main-scope signoff because it uses deferred regression/forecast work.

### Pre-existing seller-distribution frontend defect

FastAPI and Spring intentionally expose /api/sellers/distribution as the singular bucket plus
seller_count arrays. The source frontend declares and reads plural buckets, so its Plotly bar chart
falls back to implicit x positions instead of intended bucket labels. This is an existing source
frontend defect, not a Spring response mismatch. The approved migration decision preserves singular
bucket; correcting the frontend requires separate authorization.

## Container delivery

The repository now contains a runtime-only Dockerfile and .dockerignore. The image copies only the
already-built executable JAR, runs as non-root UID 10001, and never copies a database. Its database
path is supplied only at runtime through OLIST_DB_PATH:

~~~
docker build --tag olist-eda-dashboard-spring:local .
docker run --rm --publish 8080:8080 \
  --volume /absolute/path/to/olist.sqlite:/data/olist.sqlite:ro \
  --env OLIST_DB_PATH=/data/olist.sqlite \
  --env CORS_ORIGINS=http://127.0.0.1:5173 \
  olist-eda-dashboard-spring:local
~~~

No OCI runtime (docker, podman, nerdctl, buildah, kaniko, or crane) is available in this
environment, so the container image could not be built or started here. The Dockerfile uses the
multi-platform eclipse-temurin:26-jre-ubi10-minimal runtime image, which is published by the
official Eclipse Temurin image project. [Docker Hub tag details](https://hub.docker.com/_/eclipse-temurin/tags?name=ubi&page=1)

## Approved differences and scope boundary

| Item | Status |
| --- | --- |
| Successful main-scope JSON contracts | No differences: all 17 matched FastAPI on the canonical database. |
| Error responses | Intentional existing difference: Spring returns sanitized non-2xx JSON errors rather than FastAPI's mixed raw/HTTP-200 and uncaught-error behavior. |
| Synthetic missing monthly category aggregate | Both are non-successful; Spring returns the documented sanitized 500 rather than exposing FastAPI/Starlette's non-finite-number serialization exception. |
| Database configuration | Intentional deployment hardening: Spring requires an explicit absolute readable path and opens it read-only, instead of FastAPI's CWD-relative default. |
| Category weights | The approved filtered implementation is the source contract; no successful-response divergence. |
| Seller distribution spelling | Singular bucket is preserved. The frontend plural buckets mismatch is pre-existing and not changed. |

Deferred, intentionally absent Spring features remain accurately listed under Future Goals in
TODO.md:

- GET /api/delivery/trend (LOWESS)
- GET /api/sales/regression
- GET /api/sales/forecast
- GET /api/shipping/daily-average
- GET /api/customers/rfm

## Repository integrity and cutover gate

The source status still contains only its pre-existing modified frontend entry and untracked
docs/backend-architecture.md; neither was modified by this migration. The target repository does
not track SQLite databases, database sidecars, local .env files, generated parity output, or build
output. .gitignore and .dockerignore prevent those artifacts from being added to the build or
container context.

The Python backend remains available. Technical backend parity, CORS, and executable-JAR evidence
are complete. Final cutover readiness remains pending three external gates: real browser frontend
smoke testing, container build/start testing on a host with an OCI runtime, and the user's explicit
review and approval of this report. Do not remove the Python backend before those gates are closed.
