# Milestone 5 parity verification

Verified 2026-07-29 against the canonical source SQLite database at the path configured by
`OLIST_DB_PATH`. FastAPI and Spring Boot ran on separate local ports, and the semantic parity
harness compared HTTP status, exact `application/json` content type, JSON property order, labels,
array dimensions, integer-versus-floating number kinds, ordering, and every response value under
the documented floating-point tolerance.

| Endpoint | Verified source contract | Full-database result |
| --- | --- | --- |
| `GET /api/orders/hourly` | `index`/`columns`/`data`; Sunday-first 7 x 24 integer matrix; string hour labels `"0"` through `"23"` | Matched |
| `GET /api/sales/monthly` | Six 20-element arrays; local ISO timestamps from `2017-01-01T00:00:00` through `2018-08-01T00:00:00` | Matched |
| `GET /api/categories/monthly-sales` | `columns`/`index`/`data`; 22 x 5 raw-category float pivot with `0.0` missing cells | Matched |
| `GET /api/sellers/shipping-times` | Three aligned arrays; 104,572 retained rows in first-seen bucket order after per-bucket IQR filtering | Matched |
| `GET /api/categories/weights` | Ordered map of five raw categories with the approved sample-standard-deviation filter | Matched |

The live command was:

```bash
PARITY_FASTAPI_BASE_URL=http://127.0.0.1:18000 \
PARITY_SPRING_BASE_URL=http://127.0.0.1:18085 \
PARITY_ENDPOINTS=/api/orders/hourly,/api/sales/monthly,/api/categories/monthly-sales,/api/sellers/shipping-times,/api/categories/weights \
./mvnw -Dparity.enabled=true -Dtest=ConfiguredParityHarnessTest test
```

It completed with one passing parity test and zero mismatches. The ordinary full Maven suite then
completed with 77 passing tests, zero failures, and one skipped opt-in parity test.

## Monthly selected-category null edge

The canonical data has no null selected-category aggregates, so it does not affect successful
response parity. Read-only source verification with a synthetic missing selected-category value
found that pandas preserves SQL `NULL` as `NaN` in `DataFrame.to_dict(orient="list")`, and
FastAPI/Starlette then rejects it with `ValueError: Out of range float values are not JSON
compliant` instead of returning a successful JSON `null`.

The Java implementation deliberately preserves the source's non-successful behavior rather than
inventing a successful JSON `null`. It returns the repository-wide sanitized HTTP 500 response,
which is the intentional error-response difference documented in `docs/api-foundations.md`; a
focused service and controller test cover this synthetic edge. No successful canonical response
contains a missing selected-category aggregate.
