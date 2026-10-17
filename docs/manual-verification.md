# Manual verification: user isolation (A vs B)

Automated coverage lives in `CustomerIsolationIntegrationTest` and
`DashboardRevenueIntegrationTest`. Use this checklist to verify a running
deployment (local or Render) end to end.

Prerequisites: backend running, two email addresses you control (A and B).

## 1. Register two accounts

```bash
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"User A","email":"a@example.com","password":"password123"}'
# → {"token":"...","name":"User A","email":"a@example.com"}   save as TOKEN_A

curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"User B","email":"b@example.com","password":"password123"}'
# → save as TOKEN_B
```

## 2. A creates data

```bash
curl -s -X POST http://localhost:8080/api/customers \
  -H "Authorization: Bearer $TOKEN_A" -H "Content-Type: application/json" \
  -d '{"name":"A Customer","email":"acustomer@example.com","phone":"+1 555 000 0001"}'
# → note the "id" — call it CUSTOMER_ID

curl -s -X POST http://localhost:8080/api/leads \
  -H "Authorization: Bearer $TOKEN_A" -H "Content-Type: application/json" \
  -d '{"name":"A Deal","company":"Acme","value":1000,"status":"WON"}'
```

## 3. B must not see any of it

```bash
# List is empty for B
curl -s http://localhost:8080/api/customers -H "Authorization: Bearer $TOKEN_B"
# → []

# Direct reads/updates/deletes of A's customer answer 404 (not 403, not 200)
curl -s -o /dev/null -w "%{http_code}\n" \
  http://localhost:8080/api/customers/$CUSTOMER_ID -H "Authorization: Bearer $TOKEN_B"
# → 404

curl -s -o /dev/null -w "%{http_code}\n" -X PUT \
  http://localhost:8080/api/customers/$CUSTOMER_ID \
  -H "Authorization: Bearer $TOKEN_B" -H "Content-Type: application/json" \
  -d '{"name":"Hijacked","email":"h@x.com","phone":"+1"}'
# → 404

curl -s -o /dev/null -w "%{http_code}\n" -X DELETE \
  http://localhost:8080/api/customers/$CUSTOMER_ID -H "Authorization: Bearer $TOKEN_B"
# → 404

# A's dashboard shows the 1000 WON lead; B's does not
curl -s http://localhost:8080/api/dashboard/revenue -H "Authorization: Bearer $TOKEN_A"
# → last month point has revenue 1000.0
curl -s http://localhost:8080/api/dashboard/revenue -H "Authorization: Bearer $TOKEN_B"
# → all zeros
```

## 4. A's data is still intact

```bash
curl -s http://localhost:8080/api/customers/$CUSTOMER_ID -H "Authorization: Bearer $TOKEN_A"
# → 200, name "A Customer"
```

## 5. Unauthenticated access

```bash
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/customers
# → 401 {"error":"Authentication required"}

curl -s -o /dev/null -w "%{http_code}\n" \
  -H "Authorization: Bearer garbage" http://localhost:8080/api/customers
# → 401
```

All checks passing means cross-user isolation holds at the API level for the
resources tested. Repeat with tasks/emails/activities if you want full
coverage of every domain — they share the same repository-filter pattern.
