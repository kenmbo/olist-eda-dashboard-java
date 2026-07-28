# Frozen FastAPI API Contracts

This document describes successful responses observed from source commit `bd7dd09a6265767752872d0450a219982a71346e` against the canonical `data/olist.sqlite` database. The compact fixtures live in `src/test/resources/contracts/fastapi/`; the full payloads were used only for the recorded invariants.

## Rules common to all in-scope endpoints

- Method: `GET`.
- Successful status: `200`.
- Successful content type: exactly `application/json`.
- All 17 baseline bodies parse as JSON and contain zero JSON `null` values, zero `NaN` values, and no non-finite numeric values. This is an observed canonical-database result, not a claim that every query is null-proof for arbitrary data.
- Object key sequence below is the sequence emitted by FastAPI. Parallel arrays have equal lengths unless stated as a split matrix.
- The baseline was repeated in full once; all 17 raw JSON bodies were byte-identical across the two captures. Queries without `ORDER BY` still have only an observed order, which Spring must explicitly preserve rather than assume SQLite will continue to provide it.
- Integer identifiers and counts are JSON integers. Floating values are JSON numbers. Unrounded SQLite/pandas floats expose binary artifacts, such as `1233131.7199999709` and `25080.030000000017`; do not round them unless the source endpoint does.

## Numeric comparison policy

For semantic parity tests, require exact strings, key order, labels, row order, integer values, dimensions, and zero-versus-null behavior. For ordinary unrounded floating values, compare with `abs(actual - expected) <= max(1e-9, 1e-12 * max(1, abs(expected)))`.

Two source-specific rules also apply:

- `/api/sellers/review-sales` uses pandas `round(2)` before serialization. Values are mathematically two-decimal rounded, but JSON does not retain trailing zeroes (`3.9`, not `3.90`).
- `/api/categories/monthly-sales` returns pandas `to_json(orient="split")`, whose values use pandas' default 10-decimal JSON precision. Preserve the resulting numeric values and the zeroes emitted as `0.0`; do not substitute a differently rounded pivot.

## Direct SQL and column-oriented contracts

### `GET /api/orders/daily`

- Orientation and keys: list object, `day`, `order_count`; both arrays have 634 entries.
- Dates: `day` is a date-only string (`YYYY-MM-DD`), from `2016-09-04` through `2018-10-17` in the observed ascending order.
- Numbers: `order_count` is integer (`1` through `1176`).
- Ordering: source groups by day without `ORDER BY`; the two captures were identical and ascending.

### `GET /api/orders/costs`

- Orientation and keys: list object, `order_id`, `product_cost`, `shipping_cost`; all lengths are 96,478.
- Values: delivered-order IDs and summed numeric product/freight costs. The numeric sums are not rounded (`shipping_cost` reaches `1794.9600000000003`).
- Ordering: source has no `ORDER BY`; the observed first/last IDs are `00010242fe8c5a6d1ba2dd792cb16214` and `fffe41c64501cc87c801fd61db3f6244`.

### `GET /api/categories/sales`

- Orientation and keys: list object, `category`, `sales`; both lengths are 19.
- Categories are translated English names, then the literal `Other categories` aggregate. Observed order: `health_beauty`, `watches_gifts`, `bed_bath_table`, `sports_leisure`, `computers_accessories`, `furniture_decor`, `housewares`, `cool_stuff`, `auto`, `toys`, `garden_tools`, `baby`, `perfumery`, `telephony`, `office_furniture`, `stationery`, `computers`, `pet_shop`, `Other categories`.
- Numbers: delivered-order price sums are unrounded floats; the first value is `1233131.7199999709`.

### `GET /api/sellers/performance`

- Orientation and keys: list object, `seller_id`, `avg_review_score`, `total_sales`, `num_orders`; all lengths are 1,315.
- Inclusion: sellers with source `COUNT(orders.order_id) > 10`.
- Ordering: source has no `ORDER BY`; observed IDs run from `001cca7ae9ae17fb1caed9dfb1094831` to `ffff564a4f9085cd26170f4732393726`.
- Numbers: average scores and sales are unrounded floats; `num_orders` is integer.

### `GET /api/sellers/distribution`

- Orientation and keys: list object, **`bucket`** (singular), `seller_count`; both lengths are 4.
- Exact aligned values: `1-9 orders`/`1714`, `10-99 orders`/`1142`, `100-999 orders`/`226`, `1000+ orders`/`13`.
- Ordering: source has no `ORDER BY`; this observed bucket order is frozen.

### `GET /api/shipping/stages-by-city`

- Orientation and keys: list object, `city`, `approved`, `delivered_to_carrier`, `delivered_to_customer`, `estimated_delivery`; all lengths are 10.
- City order: `SALVADOR`, `PORTO ALEGRE`, `RIO DE JANEIRO`, `BRASILIA`, `BELO HORIZONTE`, `CURITIBA`, `CAMPINAS`, `SAO BERNARDO DO CAMPO`, `SAO PAULO`, `GUARULHOS`.
- Numbers: unrounded fractional Julian-day averages. This legacy query uses a fixed city list and lets SQLite `AVG` ignore null stage values.

### `GET /api/customers/clv-map`

- Orientation and keys: list object, `zip_prefix`, `avg_CLV`, `customer_count`, `latitude`, `longitude`; all lengths are 14,826.
- `avg_CLV` capitalization is contractual. `zip_prefix` and `customer_count` are integers; zip prefixes are numeric, not zero-padded strings. The coordinate and CLV values are unrounded floats.
- Ordering: source has no `ORDER BY`; observed zip prefixes begin at `1003` and end at `99990`.

## Light formatting and custom-array contracts

### `GET /api/sellers/review-sales`

- Orientation and keys: custom array object, `seller_ids`, `total_sales`, `avg_scores`, `order_counts`; all lengths are 1,647.
- Inclusion: source query has `order_count > 5`.
- Formatting: pandas rounds sales and average scores to two decimals but does not force decimal zero padding in JSON.
- Ordering: source has no `ORDER BY`; observed seller IDs begin `001cca7ae9ae17fb1caed9dfb1094831` and end `ffff564a4f9085cd26170f4732393726`.

### `GET /api/leads/conversion`

- Orientation and keys: custom array object, `origins`, `qualified_leads`, `closed_leads`, `conversion_rate`; all lengths are 10.
- Label/order: underscores are replaced and title-cased. The order is `Organic Search`, `Paid Search`, `Social`, `Unknown`, `Direct Traffic`, `Email`, `Referral`, `Other`, `Display`, `Other Publicities`.
- Numbers: lead arrays are integers; conversion rates are unrounded floats such as `11.803135888501743`.

### `GET /api/leads/origin`

- Orientation and keys: custom array object, `origins`, `leads`; both lengths are 9.
- Label/order: source excludes null, blank, and raw `other` origins, then title-cases underscores. Exact ascending-count order: `Other Publicities`, `Display`, `Referral`, `Email`, `Direct Traffic`, `Unknown`, `Social`, `Paid Search`, `Organic Search` with counts `65`, `118`, `284`, `493`, `499`, `1099`, `1350`, `1586`, `2296`.

### `GET /api/reviews/distribution`

- Orientation and keys: custom array object, `scores`, `counts`; both lengths are 5.
- Exact labels/counts: `1 ★`/`11424`, `2 ★`/`3151`, `3 ★`/`8179`, `4 ★`/`19142`, `5 ★`/`57328`. The star is literal U+2605.

### `GET /api/delivery/stages`

- Orientation and keys: custom array object, `cities`, `approval_days`, `carrier_days`, `transit_days`; all lengths are 10.
- Label/order: title-cased city names in this exact order: `Sao Paulo`, `Rio De Janeiro`, `Belo Horizonte`, `Brasilia`, `Curitiba`, `Campinas`, `Porto Alegre`, `Salvador`, `Guarulhos`, `Sao Bernardo Do Campo`.
- Numbers: unrounded fractional Julian-day averages. Unlike `/api/shipping/stages-by-city`, this query chooses the top delivered cities dynamically and excludes rows missing a stage timestamp.

## Deterministic reshaping and filtering contracts

### `GET /api/orders/hourly`

- Orientation and keys: split object, `index`, `columns`, `data`.
- Dimensions: index length 7; columns length 24; data is a 7 × 24 integer matrix.
- Exact index: `Sun`, `Mon`, `Tue`, `Wed`, `Thu`, `Fri`, `Sat`.
- Exact columns: strings `"0"` through `"23"`, not numeric column labels.
- Weekday rule: SQLite `STRFTIME('%w')` returns Sunday as `0`; therefore Sunday is first despite the stale source SQL comment saying Sunday is `7`.

### `GET /api/sales/monthly`

- Orientation and keys: list object, `year_month`, `health_beauty`, `auto`, `toys`, `electronics`, `fashion_shoes`; all lengths are 20.
- Exact date serialization: pandas timestamps serialize as ISO datetimes, not raw `YYYY-MM` strings: `2017-01-01T00:00:00` through `2018-08-01T00:00:00`, one entry per month in ascending order.
- Numbers: unrounded category sums; the baseline contains no null category cells.

### `GET /api/categories/monthly-sales`

- Orientation and keys: pandas split object in this serialized order: `columns`, `index`, `data`.
- Dimensions: five columns, 22 month rows, 22 × 5 float matrix.
- Exact columns/order: `beleza_saude`, `cama_mesa_banho`, `esporte_lazer`, `informatica_acessorios`, `relogios_presentes`.
- Exact index/order: `2016-09`, `2016-10`, `2017-01`, `2017-02`, `2017-03`, `2017-04`, `2017-05`, `2017-06`, `2017-07`, `2017-08`, `2017-09`, `2017-10`, `2017-11`, `2017-12`, `2018-01`, `2018-02`, `2018-03`, `2018-04`, `2018-05`, `2018-06`, `2018-07`, `2018-08`.
- Values: delivered-only raw Portuguese categories; missing cells are numeric `0.0`, never null. Pandas `to_json` default 10-decimal precision is observable, for example the last row is `[119391.0099999999, 60891.0900000001, 50860.1800000001, 40052.6400000001, 69767.06]`.

### `GET /api/sellers/shipping-times`

- Orientation and keys: list object, `bucket`, `seller_id`, `delivery_time`; all lengths are 104,572.
- Data shaping: delivered-order Julian-day durations are filtered independently per bucket with pandas Q1/Q3 IQR bounds and inclusive `Q1 - 1.5 × IQR` through `Q3 + 1.5 × IQR` comparisons.
- Observed contiguous bucket order/counts: `10-99 orders` (33,571), `100-999 orders` (47,697), `1000+ orders` (18,193), `1-9 orders` (5,111). This is the source's first-seen-group / `pd.concat` order, not a declared SQL order.
- Important numeric bounds: maximum retained delivery times by those groups are `28.11459490749985`, `28.41193287074566`, `32.221354166511446`, and `26.271979166660458` respectively.

### `GET /api/categories/weights`

- Orientation and keys: object map from raw Portuguese category name to a filtered float array.
- This is the **filtered** current source handler and the required migration contract. Although the older architecture document mentions duplicate handlers, current `src/main.py` has one handler and the live response is filtered.
- Top-five key order and filtered lengths: `cama_mesa_banho` (9,919), `beleza_saude` (9,221), `esporte_lazer` (7,889), `moveis_decoracao` (7,319), `informatica_acessorios` (7,121).
- Source selection/raw counts before filtering: `cama_mesa_banho` 11,115; `beleza_saude` 9,670; `esporte_lazer` 8,641; `moveis_decoracao` 8,334; `informatica_acessorios` 7,827. The join deliberately preserves repeated `order_items` observations.
- Per category, source computes arithmetic mean and pandas sample standard deviation (`ddof=1`), then retains inclusive `mean ± 0.8 × stddev`. A one-value category would be retained because comparisons against its NaN standard deviation are false. Filtered min/max values in key order are `0`/`4442`, `50`/`3350`, `50`/`4400`, `2`/`5700`, `50`/`2300`.

## Full-response fingerprints and large invariants

The following byte counts and SHA-256 values are for full successful FastAPI bodies, not the compact fixtures.

| Endpoint | HTTP | Content-Type | Bytes | SHA-256 |
| --- | ---: | --- | ---: | --- |
| `/api/orders/daily` | 200 | `application/json` | 10,599 | `91a5e1e785ea8dd29c9656d320aba8f4613e95226076cbaa5b0c2b96f7a0b121` |
| `/api/orders/costs` | 200 | `application/json` | 4,501,747 | `3704ce43852b8fdf228326c878368ed5328a55d9825919cc56e1f165c76b0988` |
| `/api/categories/sales` | 200 | `application/json` | 641 | `cfa422c4ee460f4fa75bd44e98b8fc4a2d47041705a26c127c08c9a13995e96d` |
| `/api/sellers/performance` | 200 | `application/json` | 90,718 | `9ae3998fd2c1323e9054fa3eb1861125ef7ae2577ace2dda5b0f526a80faea3e` |
| `/api/sellers/distribution` | 200 | `application/json` | 106 | `f9d48999d9abf136328bed14a864d5b6b19b3b25d10a7bd082296b2996201ae7` |
| `/api/shipping/stages-by-city` | 200 | `application/json` | 982 | `ce00d7a3876f360a2755dc98387809ed2d2555a482b122a7cba11e7fb923fa88` |
| `/api/customers/clv-map` | 200 | `application/json` | 884,713 | `3f8b1de784422b199aca7ed5d78314cabce052622cd862fb13fae93bb98ce0ff` |
| `/api/sellers/review-sales` | 200 | `application/json` | 82,685 | `04210e48f2ad8773cac674fc9eda9a10a212476fac965968ae88aff86a48f281` |
| `/api/leads/conversion` | 200 | `application/json` | 452 | `6b31a024af1d16a73173abf88bcc285698caf55b1fc97236a942a793e0cbe1ca` |
| `/api/leads/origin` | 200 | `application/json` | 178 | `87f8a43c958e5cc9b1a06888e45b5cb6d1399f1387a51d2a996e91fea0f418f4` |
| `/api/reviews/distribution` | 200 | `application/json` | 91 | `4f4a928ca981f71db435c5a7ef495b2e6f0cc8791fa464555faffd4f92e3ccab` |
| `/api/delivery/stages` | 200 | `application/json` | 766 | `a8ffdfc35a795eed0a280e5e54aa15110a1b1c7ecac80402b3982416f08b77c9` |
| `/api/orders/hourly` | 200 | `application/json` | 856 | `3403b963f489104820ec884d8c078bb1d44f5d4fdad7a9df822fbf8d79d94d55` |
| `/api/sales/monthly` | 200 | `application/json` | 2,261 | `6d20e015f296806d4e2bc0cbc943fcc72b0a35edb54ed5e431fd851095515671` |
| `/api/categories/monthly-sales` | 200 | `application/json` | 1,902 | `fa147840a01aa4935dd6e63a1c7a7633f77d8604fbb457b7accb2593165b6dab` |
| `/api/sellers/shipping-times` | 200 | `application/json` | 7,235,374 | `e7b9a0de3bcd8f30f395aedb47d2e681dd7aa8f4ebfe9c14005193efd917b24b` |
| `/api/categories/weights` | 200 | `application/json` | 263,200 | `77b2b8a519e3745f6eb310bb4fa9a76065ac74f4f38e09266331817a5c5eb207` |

## Deferred endpoints: excluded from main-migration parity

| Endpoint | Reason it is deferred |
| --- | --- |
| `GET /api/delivery/trend` | LOWESS calculation |
| `GET /api/sales/regression` | Sales regression |
| `GET /api/sales/forecast` | Sales forecasting |
| `GET /api/shipping/daily-average` | Source references missing `queries.daily_avg_shipping_time` |
| `GET /api/customers/rfm` | Source references missing `queries.rfm_buckets` |

Their absence from Spring during the main migration is not a parity failure. No fixture or successful-response target was created for them.

## Resolved source/frontend discrepancy

The live FastAPI contract for `/api/sellers/distribution` is `{ "bucket": [...], "seller_count": [...] }`, while the read-only source frontend declares and reads `data.buckets` (plural) in `frontend/src/types/api.ts` and `frontend/src/features/sellers/SellerDistributionChart.tsx`. The frontend receives raw JSON without runtime validation, so the chart falls back to implicit Plotly x positions rather than the intended bucket labels.

Decision recorded 2026-07-22: preserve the successful FastAPI contract as singular `bucket` in Spring. Do not rename it to `buckets` or make a coordinated frontend/API change as part of this migration without separate approval.

The current frontend mismatch remains a source-side defect to document, not a reason to alter the frozen API contract.
