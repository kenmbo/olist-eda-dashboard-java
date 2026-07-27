# FastAPI Source Baseline

This is the reproducible source reference for the initial Spring migration. It was captured on 2026-07-22 from live HTTP requests to FastAPI; it does not contain a copy of the Olist database.

## Identity

| Item | Recorded value |
| --- | --- |
| Source repository | `/home/kebo/python/olist-migration/olist-eda-dashboard` |
| Source commit | `bd7dd09a6265767752872d0450a219982a71346e` (`refactor(api): remove duplicate categories/weights endpoint`) |
| Source worktree at capture | Branch `migartion`; pre-existing modified `frontend` submodule only. The source backend and frontend were not changed for this baseline. |
| Canonical database filename | `data/olist.sqlite` (basename: `olist.sqlite`) |
| Database size | 112,701,440 bytes |
| Database SHA-256 | `49446afd935721ee12fc95316fbee9666a3e1bd4872dfa194fe4625d6762a81a` |
| Source runtime used | Python 3.11.2, FastAPI 0.139.2, Uvicorn 0.51.0, pandas 3.0.3 |

The SHA-256 was measured directly from the canonical source file before documenting the baseline. The database was neither copied into this repository nor modified.

## Reproduction method

The source application must start from the source repository root because `database.get_connection()` defaults to the CWD-relative `data/olist.sqlite` path. The baseline used the source virtual environment and disabled bytecode writes:

```bash
cd /home/kebo/python/olist-migration/olist-eda-dashboard
PYTHONDONTWRITEBYTECODE=1 .venv/bin/python -B -m uvicorn src.main:app \
  --host 127.0.0.1 --port 8000 --log-level warning
```

Each of the 17 in-scope routes was requested directly over HTTP with `curl`, rather than inferred from OpenAPI. Every response body parsed as JSON and each successful response had the exact content type `application/json`.

The full capture was then repeated once. The two bodies for every one of the 17 endpoints had identical SHA-256 hashes, so no unstable ordering was observed in this environment. This does not turn a SQL query without `ORDER BY` into a database guarantee: its observed order is the migration baseline and must be reproduced deliberately.

## Captured artifacts

The 17 compact, parseable representative fixtures are under `src/test/resources/contracts/fastapi/`.

- Small responses are retained in full.
- Large column-oriented responses retain the first three and last three aligned rows.
- The category-weight fixture retains the first three and last three filtered weights for each of the five keys.
- Complete payload byte counts, SHA-256 fingerprints, array lengths, and matrix dimensions are recorded in [api-contracts.md](api-contracts.md), so no full multi-megabyte API payload or database file is committed.

The fixtures preserve field spelling, top-level key order, representative labels, date serialization, and representative numeric precision. They are examples, not substitutes for the full-response invariants.

## Scope result

FastAPI started successfully against the canonical database. All 17 main-migration endpoints returned HTTP 200 with `application/json`; none returned a source blocker. The five deferred endpoints are explicitly excluded in [api-contracts.md](api-contracts.md) and were not treated as migration-parity failures.

No Maven command was run for this milestone: the repository intentionally has no Spring/Maven scaffold or Maven Wrapper until Milestone 1.
