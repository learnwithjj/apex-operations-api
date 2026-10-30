#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"
for i in $(seq 1 60); do
  if curl --fail --silent "$BASE_URL/actuator/health" | grep -q '"status":"UP"'; then break; fi
  if [ "$i" -eq 60 ]; then echo "API did not become healthy"; exit 1; fi
  sleep 2
done
response="$(curl -fsS -X POST "$BASE_URL/api/operations" -H 'Content-Type: application/json' -d '{"name":"Smoke test","description":"CI release verification","status":"PENDING"}')"
id="$(printf '%s' "$response" | python3 -c 'import json,sys; print(json.load(sys.stdin)["id"])')"
curl -fsS "$BASE_URL/api/operations/$id" >/dev/null
curl -fsS -X PUT "$BASE_URL/api/operations/$id" -H 'Content-Type: application/json' -d '{"name":"Smoke test","status":"COMPLETED"}' >/dev/null
curl -fsS -X DELETE "$BASE_URL/api/operations/$id" >/dev/null
echo "Smoke test passed"
