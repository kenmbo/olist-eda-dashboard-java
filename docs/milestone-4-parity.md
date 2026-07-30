# Milestone 4 Light Formatting Parity

Milestone 4 ports the five successful-response contracts that combine direct
SQLite results with pandas presentation work. Both live backends used the
canonical read-only source database recorded in
[the migration baseline](migration-baseline.md).

## Verified full-database results

On 2026-07-29, FastAPI and Spring were compared as complete parsed JSON
responses. Every endpoint returned HTTP `200` and exactly `application/json`.
The semantic comparator requires exact root-key sequence, labels, array order
and length, null behavior, and integer-versus-floating JSON number kinds.
Floating values use the shared `max(1e-9, 1e-12 * max(1, abs(expected)))`
tolerance.

| Endpoint | Frozen full-response invariant | Result |
| --- | --- | --- |
| `GET /api/sellers/review-sales` | `seller_ids`, `total_sales`, `avg_scores`, and `order_counts`: 1,647 values | Match |
| `GET /api/leads/conversion` | Four custom arrays: 10 values | Match |
| `GET /api/leads/origin` | `origins` and `leads`: 9 values | Match |
| `GET /api/reviews/distribution` | `scores` and `counts`: 5 values | Match |
| `GET /api/delivery/stages` | Four custom arrays: 10 values | Match |

## Formatting evidence

### Seller rounding

The source applies `pandas.Series.round(2)` to total sales and average review
scores. A read-only Python check established the relevant NumPy/pandas scaled
IEEE-754 behavior before implementation:

| Input | pandas result |
| ---: | ---: |
| `1.125` | `1.12` |
| `1.375` | `1.38` |
| `1.245` | `1.25` |
| `2.675` | `2.68` |
| `-1.125` | `-1.12` |

`PandasFormattingService` uses scaled `Math.rint`, which applies the same
half-to-even IEEE-754 operation. Unit tests cover those observed ties and the
complete 1,647-row live response matches FastAPI.

### Labels and ordering

- Lead origins replace underscores with spaces and use pandas-compatible title
  casing, including `Other Publicities` and `Direct Traffic`.
- Lead conversion retains source `COALESCE(origin, 'unknown')`, so `Unknown`
  is still included; lead-origin separately excludes raw null, blank, and
  `other` values before formatting.
- Review scores retain the literal U+2605 star labels, from `1 ★` through
  `5 ★`.
- Delivery cities retain the source top-delivered-city ordering, then use title
  casing such as `Sao Bernardo Do Campo`. The source SQL's complete-stage
  timestamp filter and unrounded `JULIANDAY` averages are unchanged.

The seller review-sales source query has no `ORDER BY`; its SQL resource was
copied without adding one. Full parity confirms the captured ascending seller
order on the canonical database.

## Commands run

Focused Milestone 4 tests:

```bash
MAVEN_USER_HOME=/tmp/olist-m4-maven-home ./mvnw \
  -Dmaven.repo.local=/tmp/olist-m3-m2 \
  -Dtest=PandasFormattingServiceTests,LightFormattingServicesTests,LightFormattingRepositoriesIntegrationTests,LightFormattingEndpointsMockMvcTests test
```

Result: 14 tests passed.

Complete project suite:

```bash
MAVEN_USER_HOME=/tmp/olist-m4-maven-home ./mvnw \
  -Dmaven.repo.local=/tmp/olist-m3-m2 test
```

Result: 58 tests passed; the opt-in live parity test was skipped in the
ordinary suite.

Full live comparison after starting FastAPI and Spring separately with the
canonical database:

```bash
PARITY_FASTAPI_BASE_URL=http://127.0.0.1:18000 \
PARITY_SPRING_BASE_URL=http://127.0.0.1:18085 \
PARITY_ENDPOINTS=/api/sellers/review-sales,/api/leads/conversion,/api/leads/origin,/api/reviews/distribution,/api/delivery/stages \
MAVEN_USER_HOME=/tmp/olist-m4-maven-home ./mvnw \
  -Dmaven.repo.local=/tmp/olist-m3-m2 \
  -Dparity.enabled=true -Dtest=ConfiguredParityHarnessTest test
```

Result: one parity test passed with zero mismatches across all five complete
responses. The temporary FastAPI and Spring processes were stopped after
verification.

## Scope boundary

No Milestone 5 endpoint or deferred statistical/forecast functionality was
added. The source repository remains read-only; independently present source
worktree changes were not included in this target change set.
