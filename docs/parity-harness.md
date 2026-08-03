# Semantic Parity Harness

`src/test/java/com/olist/dashboard/parity/` contains the repeatable live parity harness. It does
not hardcode ports, database paths, or a source checkout location.

## Run it

Start FastAPI and Spring separately, then supply both base URLs explicitly:

```bash
PARITY_FASTAPI_BASE_URL=http://127.0.0.1:8000 \
PARITY_SPRING_BASE_URL=http://127.0.0.1:8080 \
PARITY_ENDPOINTS=/api/orders/daily,/api/orders/costs \
./mvnw -Dparity.enabled=true -Dtest=ConfiguredParityHarnessTest test
```

The same settings can be passed as Maven system properties:

- `parity.fastapi-base-url`
- `parity.spring-base-url`
- `parity.endpoints` (optional comma-separated subset)

When `PARITY_ENDPOINTS` is absent, the harness selects the 17 frozen main-scope endpoints. The
five deferred endpoints are deliberately not in that catalog. The live test is disabled unless
`-Dparity.enabled=true` is present, so ordinary unit tests never require running servers.

## What it compares

For each selected endpoint, the harness requires HTTP 200 and exactly `application/json` from both
backends. It parses complete response bodies and compares:

- object field names and insertion order;
- array order and length, including split-matrix dimensions;
- text, boolean, and null values exactly;
- JSON integer versus floating-number kind and integer values exactly;
- finite floating values with the documented absolute/relative tolerance.

Mismatch output names the endpoint and JSON path. It intentionally compares live full responses,
not just compact fixture snippets. Error responses are excluded because Spring's non-2xx sanitized
error policy is an approved divergence from FastAPI's mixed error behavior.
