# Apex Operations API

Java 21 / Spring Boot 3.5.7 REST API with PostgreSQL, Flyway migrations, Docker Compose, Prometheus, Grafana and GitHub Actions.

## Prerequisites

Docker Engine + Docker Compose v2; `curl` and Python 3 for the smoke test. Java 21 + Maven 3.9+ for local compilation. Docker must be running for Testcontainers integration tests.

## Quick start

```bash
cp .env.example .env
docker compose up --build -d
bash scripts/smoke-test.sh
```

API: http://localhost:8080/api/operations
Health: http://localhost:8080/actuator/health
Metrics: http://localhost:8080/actuator/prometheus
Prometheus: http://localhost:9090
Grafana: http://localhost:3000 (admin / value of `GRAFANA_PASSWORD` in `.env`).

All published service ports bind to localhost. This project has no authentication: **do not expose it to untrusted networks**. Change local sample passwords and never commit `.env`.

## Example API calls

```bash
curl -i -X POST http://localhost:8080/api/operations \
  -H 'Content-Type: application/json' \
  -d '{"name":"Nightly backup","description":"DB backup","status":"PENDING"}'
curl http://localhost:8080/api/operations
curl http://localhost:8080/api/operations/1
curl -X PUT http://localhost:8080/api/operations/1 \
  -H 'Content-Type: application/json' \
  -d '{"name":"Nightly backup","status":"COMPLETED"}'
curl -i -X DELETE http://localhost:8080/api/operations/1
```

Status values: `PENDING`, `RUNNING`, `COMPLETED`, `FAILED`. Unknown IDs return HTTP 404; invalid request bodies return HTTP 400.

## Tests

```bash
mvn -B verify
```

Unit tests use Mockito. The integration test runs a real ephemeral PostgreSQL container using Testcontainers and verifies CRUD, validation and Flyway migration.

## Release pipeline

Pull requests run tests. Commits to `main` and `v*` tags run tests and publish an image to GHCR:
- `ghcr.io/<owner>/<repo>:sha-<full-commit-sha>`
- `ghcr.io/<owner>/<repo>:main` or `:v1.0.0`

The SHA tag is the recommended release selector. Tags are references; for stronger supply-chain guarantees, deploy a verified image digest and protect tag mutation in the registry.

To deploy a published image locally, ensure you can pull it (`docker login ghcr.io` for private packages), then:

```bash
# In .env set:
# API_IMAGE=ghcr.io/<owner>/<repo>:sha-<full-commit-sha>
docker compose pull api
docker compose up -d --no-build api
bash scripts/smoke-test.sh
```

Rollback: restore the previous `API_IMAGE` in `.env`, then run `docker compose pull api && docker compose up -d --no-build api` and rerun smoke tests. Do not roll back database schema blindly; this sample migration is additive only at initial deployment.

GitHub-hosted runners publish images but **do not deploy to your local laptop**. Local release promotion is explicit and reproducible.

## Operation and troubleshooting

```bash
docker compose ps
docker compose logs -f api
docker compose logs -f db
curl -fsS http://localhost:8080/actuator/health
curl -fsS http://localhost:9090/-/healthy
docker compose down              # keeps data
docker compose down -v           # DELETES database and Grafana volumes
```

Grafana automatically provisions the Prometheus datasource. Try the `http_server_requests_seconds_count` metric in Explore after sending requests.

## Security and scope

This is a small local demonstration, not a hardened internet-facing API. No authentication or TLS; Actuator metrics are exposed on the local host. Credentials are illustrative defaults. Pin image digests, add auth/TLS, secret management, image scanning, database backups, and deployment approval controls for production.

## Known verification limitation

Source and config are provided for execution; build and container execution should be verified on a machine with Maven, Docker and registry access.
