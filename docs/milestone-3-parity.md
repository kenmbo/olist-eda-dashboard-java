# Milestone 3 Direct SQL Parity

Milestone 3 ports the seven direct-SQL, column-oriented successful-response
contracts. The canonical SQLite database is the read-only source database
identified by the checksum in [the migration baseline](migration-baseline.md).
No database copy was added to this repository.

## Verified full-database results

On 2026-07-24, the live semantic parity harness compared FastAPI and Spring
against that same database. Each response returned HTTP `200` and exactly
`application/json`; the harness then compared complete parsed responses. It
requires exact object-key sequence, array lengths and order, strings, nulls,
and integer-versus-floating JSON number kinds. Floating values use the shared
`max(1e-9, 1e-12 * max(1, abs(expected)))` tolerance.

| Endpoint | Frozen full-response invariant | Result |
| --- | --- | --- |
| `GET /api/orders/daily` | `day` and `order_count`: 634 values | Match |
| `GET /api/orders/costs` | `order_id`, `product_cost`, and `shipping_cost`: 96,478 values | Match |
| `GET /api/categories/sales` | `category` and `sales`: 19 values, including final `Other categories` | Match |
| `GET /api/sellers/performance` | Four arrays: 1,315 values | Match |
| `GET /api/sellers/distribution` | singular `bucket` and `seller_count`: four aligned values | Match |
| `GET /api/shipping/stages-by-city` | five arrays: 10 values, frozen city sequence | Match |
| `GET /api/customers/clv-map` | five arrays: 14,826 values; `avg_CLV` spelling preserved | Match |

The canonical shipping cities and CLV zip-prefix boundary values remained the
captured values: shipping has 10 rows, while CLV begins at numeric zip prefix
`1003` and ends at `99990`. Counts and identifiers serialize as JSON integers;
the source's `SUM` and `AVG` values serialize as unrounded JSON floating
numbers.

## Source-ordering evidence

The source SQL for daily orders, order costs, category sales, seller
performance, seller distribution, and the CLV map does not supply an outer
`ORDER BY` for every returned row set. The frozen FastAPI captures were stable
across repeated requests, and full live parity above confirms the Spring
responses preserve that observed order on the canonical database.

Two seller queries need an explicit Spring ordering to retain the already
captured successful contract across JDBC execution:

- Seller performance is ordered by ascending `seller_id`.
- Seller distribution is ordered by the observed volume buckets: `1-9 orders`,
  `10-99 orders`, `100-999 orders`, then `1000+ orders`.

These are contract stabilizations, not new API behavior: the source responses
have those exact orders in the frozen baseline, and the combined live parity
comparison matched every element. The distribution property remains the
approved singular `bucket`, never `buckets`.

The CLV SQL resource preserves the source's nested `WITH CLV AS (WITH
CustomerData ...)` form; SQLite JDBC executed that literal source structure in
the isolated repository test and live Spring check.

## Commands run

The full unit, repository, and MockMvc suite was run with Java 26:

```bash
MAVEN_USER_HOME=/tmp/olist-m3-maven-home ./mvnw \
  -Dmaven.repo.local=/tmp/olist-m3-m2 test
```

Result: 44 tests passed; the opt-in live parity test was skipped in that
ordinary suite.

For the full live comparison, FastAPI and Spring were started separately with
the canonical database, then this command was run:

```bash
PARITY_FASTAPI_BASE_URL=http://127.0.0.1:18000 \
PARITY_SPRING_BASE_URL=http://127.0.0.1:18085 \
PARITY_ENDPOINTS=/api/orders/daily,/api/orders/costs,/api/categories/sales,/api/sellers/performance,/api/sellers/distribution,/api/shipping/stages-by-city,/api/customers/clv-map \
MAVEN_USER_HOME=/tmp/olist-m3-maven-home ./mvnw \
  -Dmaven.repo.local=/tmp/olist-m3-m2 \
  -Dparity.enabled=true -Dtest=ConfiguredParityHarnessTest test
```

Result: one parity test passed with zero mismatches across all seven complete
responses. The temporary Spring process used for this check is stopped after
the verification.

## Scope boundary

No Milestone 4 or 5 endpoint was added. LOWESS, regression, forecasting, RFM,
and daily shipping average remain out of scope as recorded in `TODO.md`.

The source repository was treated as read-only. Any independently present
source-worktree changes were neither changed nor included in this Spring
milestone.
