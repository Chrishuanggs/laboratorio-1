# Lecture Guide: Spring Security + Keycloak

Instructor notes for a ~90-minute session. Each section says **what to show** and **what to say**.

---

## 0. Before class (10 min, do it at home first)

```bash
# 1. Start Postgres + Keycloak
docker compose -f docker/docker-compose.yml up -d

# 2. Wait until Keycloak is ready (~15 s)
docker logs -f keycloak        # wait for: "Realm 'veterinaria' imported" ... "started in"

# 3. Run the API (OPENAI_API_KEY is required by Spring AI; any value works if you don't demo the chat)
OPENAI_API_KEY=dummy ./mvnw spring-boot:run
```

| What | URL | Credentials |
|---|---|---|
| API | http://localhost:8080 | — |
| Keycloak admin console | http://localhost:8180 | `admin` / `admin` |
| Realm | `veterinaria` | — |
| Users | — | `ana`/`ana123` (ADMIN), `victor`/`victor123` (VETERINARIO), `carla`/`carla123` (CLIENTE) |

Have `http/seguridad.http` open in IntelliJ (or use the curl commands at the bottom).

> **Reset everything:** `docker compose -f docker/docker-compose.yml up -d --force-recreate keycloak`
> Keycloak runs in dev mode without a volume, so recreating the container re-imports the realm from
> `docker/keycloak/realm-veterinaria.json` and erases any changes you made in class.

---

## 1. Concepts (whiteboard, 15 min)

### Authentication vs. Authorization
- **Authentication (AuthN):** *Who are you?* Proving identity with a password, MFA, etc. → **401 Unauthorized**
- **Authorization (AuthZ):** *What are you allowed to do?* → **403 Forbidden**
- Quick trick: 401 = "I don't know you", 403 = "I know you, but you can't do this".

### Why not just store users in our own database?
- Every app would re-implement login, password hashing, password reset, MFA, lockouts and so on.
- An **Identity Provider (IdP) / auth broker** like Keycloak does it once, for every app.
  This enables single sign-on and login with Google/GitHub/LDAP (that's the "broker" part).
- Our API **never sees a password**. It only checks a signed token.

### The flow (draw this)

```
 ┌────────┐  1. username/password   ┌──────────┐
 │ Client │ ──────────────────────▶ │ Keycloak │
 │(Postman│ ◀────────────────────── │  (IdP)   │
 │  /web) │  2. access_token (JWT)  └──────────┘
 └────────┘                               ▲
     │ 3. GET /api/v1/duenno               │ 4. (once at startup/first request)
     │    Authorization: Bearer <JWT>      │    download public keys (JWKS)
     ▼                                     │
 ┌──────────────────────────┐              │
 │ Spring Boot API          │──────────────┘
 │ (OAuth2 Resource Server) │  5. verify signature + expiration + issuer locally
 └──────────────────────────┘  6. check roles/permissions → 200 / 403
```

Key point: **step 5 does not call Keycloak on each request.** The API verifies the JWT signature with
Keycloak's *public* key. That's why it scales.

### JWT in 2 minutes
- Three Base64 parts: `header.payload.signature`. Paste one at https://jwt.io in class.
- The payload is **readable by anyone**. It's signed, not encrypted, so never put secrets in it.
- The signature guarantees nobody modified it. If you change one character, the API rejects it with 401.
- It **expires** (15 min here, `accessTokenLifespan: 900`).

### Roles vs. Permissions (the core of this lecture)
- **Role** = *who you are in the organization*: `ADMIN`, `VETERINARIO`, `CLIENTE`.
- **Permission** = *a specific action*: `duenno:leer`, `duenno:escribir`, `duenno:eliminar`, `perro:leer`, `chat:usar`.
- A role is a **bundle of permissions**. In Keycloak this is a **composite role**.

| Permission ↓ / Role → | ADMIN | VETERINARIO | CLIENTE |
|---|:-:|:-:|:-:|
| `perro:leer`        | ✅ | ✅ | ✅ |
| `duenno:leer`       | ✅ | ✅ | ❌ |
| `duenno:escribir`   | ✅ | ✅ | ❌ |
| `duenno:eliminar`   | ✅ | ❌ | ❌ |
| `chat:usar`         | ✅ | ✅ | ❌ |

**Why prefer permissions in code?** If tomorrow "VETERINARIO can also delete", you change it in Keycloak
with **zero code changes and no redeploy**. If the code says `hasRole('ADMIN')`, you must edit and redeploy.

---

## 2. Tour of Keycloak (15 min, live in the browser)

Open http://localhost:8180 → `admin`/`admin` → choose realm **veterinaria** (top-left dropdown).

1. **Realm**: an isolated tenant (its own users, roles, clients). Never use `master` for apps.
2. **Clients → `persistencia-api`**: the application registered with Keycloak.
   - *Client authentication: OFF* → public client (no secret), which keeps the demo simple.
   - *Direct access grants: ON* → allows the username/password grant we use from curl/IntelliJ.
     (In a real web/mobile app you'd use **Authorization Code + PKCE**, where the user logs in on Keycloak's page.
     Mention this, but don't go deep.)
   - **Roles tab** → the 5 permissions live here as *client roles*.
3. **Realm roles** → `ADMIN`, `VETERINARIO`, `CLIENTE`. Open `VETERINARIO` → *Associated roles*. Point out
   that it's composite and includes 4 client roles.
4. **Users → victor → Role mapping** → only `VETERINARIO` is assigned. Toggle *Hide inherited roles* off
   to show the permissions it inherits.

---

## 3. Get a token and read it (10 min)

Run request **#1** in `http/seguridad.http` (or the curl below). Copy `access_token` into https://jwt.io.

Point at these claims:

```json
"iss": "http://localhost:8180/realms/veterinaria",   ← who issued it (the API validates this)
"exp": 1759700000,                                    ← expiration
"preferred_username": "victor",
"realm_access":    { "roles": ["VETERINARIO", ...] },             ← ROLES
"resource_access": { "persistencia-api": { "roles": [             ← PERMISSIONS
     "perro:leer", "duenno:leer", "duenno:escribir", "chat:usar" ] } }
```

---

### Logging in through the API: `POST /api/v1/auth/login`
Instead of calling Keycloak directly, clients can send `{"username","password"}` to the API.
`AuthController` → `AuthService` forwards them to Keycloak's token endpoint (`keycloak.token-uri`) and returns
Keycloak's response as-is (`access_token`, `refresh_token`, `token_type`, `expires_in`). Wrong credentials → 401.

Say this explicitly: **the API still never checks the password.** It only relays the request to Keycloak. The endpoint is
`permitAll()` in `SecurityConfig`, because you can't require a token to get a token.
It uses the password grant, which is fine for a demo. A real frontend would redirect to Keycloak's login page instead.

## 4. The Spring side (20 min, walk through the code in this order)

### 4.1 `pom.xml`: one dependency
`spring-boot-starter-security-oauth2-resource-server` adds Spring Security plus JWT validation.
**Demo moment:** as soon as you add Spring Security, *everything* is locked by default (secure by default).

### 4.2 `application.yml`: one line that does a lot
```yaml
spring.security.oauth2.resourceserver.jwt.issuer-uri: http://localhost:8180/realms/veterinaria
```
Spring reads `<issuer>/.well-known/openid-configuration`, finds the JWKS URL, downloads the public keys,
and validates signature, `exp` and `iss` on every token. Open that URL in the browser in class.

### 4.3 `security/SecurityConfig.java`: URL-level rules
- `csrf.disable()` + `STATELESS`: REST API with tokens, no cookies or sessions.
- `.requestMatchers("/api/v1/demo/publico", ...).permitAll()` → public.
- `.anyRequest().authenticated()` → everything else requires a valid token (or the API returns 401).
- `@EnableMethodSecurity(securedEnabled = true)` → turns on `@PreAuthorize` and `@Secured`.
  **Without this, the annotations are silently ignored.** That's a classic bug and worth saying out loud.

### 4.4 `security/KeycloakJwtConverter.java`: the translator
Spring only reads the standard `scope` claim by default. Keycloak puts roles elsewhere, so we translate:
- `realm_access.roles` → `ROLE_ADMIN` (the `ROLE_` prefix is what `hasRole()` looks for)
- `resource_access.persistencia-api.roles` → `duenno:leer` (used with `hasAuthority()`)

Rule to memorize: **`hasRole('X')` == `hasAuthority('ROLE_X')`**.

### 4.5 `controllers/DemoSeguridadController.java`: one endpoint per concept

| Endpoint | Rule | Concept |
|---|---|---|
| `GET /demo/publico` | `permitAll()` in config | Public |
| `GET /demo/yo` | just authenticated | Shows what Spring sees in the token |
| `GET /demo/admin` | `@PreAuthorize("hasRole('ADMIN')")` | Role |
| `GET /demo/personal` | `@PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")` | Several roles |
| `GET /demo/veterinario` | `@Secured("ROLE_VETERINARIO")` | Older/simpler annotation |
| `GET /demo/permiso-eliminar` | `@PreAuthorize("hasAuthority('duenno:eliminar')")` | Permission |
| `GET /demo/perfil/{username}` | `@PreAuthorize("#username == authentication.name or hasRole('ADMIN')")` | Rule using request data (ownership) |

### 4.6 Real endpoints: `DuennoController`, `PerroController`, `ChatController`
All use **permissions** (`hasAuthority`). The pattern is *read / write / delete*. Students should copy this
for their projects.

---

## 5. Live demo script (15 min): the "aha" moments

Use `http/seguridad.http`; change the username in request #1 to switch users.

| # | Do this | Expect | Say this |
|---|---|---|---|
| 1 | `GET /demo/publico` without a token | **200** | Public route |
| 2 | `GET /demo/yo` without a token | **401** | "I don't know you" |
| 3 | Login as **carla**, `GET /demo/yo` | 200, authorities: `ROLE_CLIENTE`, `perro:leer` | This is what Spring extracted from the JWT |
| 4 | carla → `GET /perro/` | **200** | Has `perro:leer` |
| 5 | carla → `GET /duenno` | **403** | "I know you, but you can't do this" |
| 6 | carla → `GET /demo/perfil/carla` → 200, then `/demo/perfil/ana` → **403** | | Ownership rule |
| 7 | Login as **victor** → `GET /duenno` → 200; `DELETE /duenno/9999` → **403** | | Can write but not delete |
| 8 | Login as **ana** → `DELETE /duenno/9999` → **404** | | Authorization passed; the record just doesn't exist |
| 9 | ana → `GET /demo/veterinario` | **403** ⚠️ | **Roles aren't hierarchical.** ADMIN isn't automatically VETERINARIO. That's another reason to check permissions |
| 10 | Change one character of the token, call `/demo/yo` | **401** | Signature check |

### 🎯 The big finale: change permissions without touching code
1. With **victor**, `DELETE /duenno/9999` → **403**.
2. In Keycloak: *Realm roles → VETERINARIO → Associated roles → Assign role → Filter by clients →*
   `duenno:eliminar` → Assign.
3. **Get a new token** for victor (the old one still has the old permissions until it expires, which is a great question to raise).
4. `DELETE /duenno/9999` → **404** (allowed now). No code change, no restart.

Optional: create a new user live (*Users → Add user → Credentials → set password, Temporary OFF →
Role mapping → CLIENTE*). Fill in email, first and last name, or Keycloak will ask to "complete profile"
and the password login will fail.

---

## 6. Likely student questions

- **"Does the API call Keycloak on every request?"** No. It downloads the public keys once and validates locally.
- **"If I remove a role, is it effective immediately?"** Not for tokens already issued. They're valid until `exp`.
  That's why access tokens are short-lived, and refresh tokens get new ones.
- **"Where's the login page?"** Our API has none on purpose. A frontend (Angular/React) would redirect to
  Keycloak's login page (Authorization Code + PKCE) and then send the token to the API.
- **"Is the password grant OK in production?"** No. It's deprecated in OAuth 2.1. We use it only because it's easy to demo from curl.
- **"@PreAuthorize vs @Secured vs SecurityConfig rules?"** Config = broad URL rules; `@Secured` = simple role
  list; `@PreAuthorize` = full expressions (roles, permissions, parameters). Prefer `@PreAuthorize`.
- **"Can I put annotations on the service instead of the controller?"** Yes. Method security works on any
  Spring bean, and it's often better because it also protects calls that don't come through HTTP.

---

## 7. Troubleshooting

| Symptom | Cause / Fix |
|---|---|
| `{"error":"invalid_request","error_description":"HTTPS required"}` | Realm must have `sslRequired: none` (already set). If you created the realm by hand: *Realm settings → General → Require SSL → None* |
| Admin console at :8180 says `HTTPS required` | The `master` realm (admin console) defaults to HTTPS for non-local requests. `docker/keycloak/start.sh` sets it to `NONE` at startup; check `docker logs keycloak` for `>>> Realm master: sslRequired=NONE`. Manual fix: `docker exec keycloak /opt/keycloak/bin/kcadm.sh config credentials --server http://localhost:8080 --realm master --user admin --password admin && docker exec keycloak /opt/keycloak/bin/kcadm.sh update realms/master -s sslRequired=NONE` |
| `Account is not fully set up` on token request | User is missing email/first name/last name, or has a required action pending |
| Always 401 with a valid-looking token | Token expired (15 min), or it was issued for `127.0.0.1` instead of `localhost` (`iss` mismatch). Always use `localhost` |
| 403 when you expected 200 | Call `/demo/yo` and look at `authorities` |
| Annotations ignored (everything returns 200) | `@EnableMethodSecurity` missing |
| App fails at startup: `OPENAI_API_KEY` | Export any value: `OPENAI_API_KEY=dummy` |
| Port 8180 in use | Change the left side of `"8180:8080"` in compose **and** `issuer-uri` |

---

## Curl cheat sheet

```bash
# Get a token through the API
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login -H 'Content-Type: application/json' \
  -d '{"username":"victor","password":"victor123"}' | jq -r .access_token)

# ...or directly from Keycloak (change user: ana / victor / carla, password = <user>123)
TOKEN=$(curl -s -X POST http://localhost:8180/realms/veterinaria/protocol/openid-connect/token \
  -d grant_type=password -d client_id=persistencia-api \
  -d username=victor -d password=victor123 | jq -r .access_token)

curl -i http://localhost:8080/api/v1/demo/publico
curl -i http://localhost:8080/api/v1/demo/yo                                   # 401
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/demo/yo | jq
curl -i -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/demo/admin   # 403 for victor
curl -i -X DELETE -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/duenno/9999
```

## Files touched (for your reference)

- `pom.xml`: resource-server starter
- `docker/docker-compose.yml`: `keycloak` service (port 8180)
- `docker/keycloak/start.sh`: starts Keycloak and allows HTTP on the `master` realm (admin console)
- `docker/keycloak/realm-veterinaria.json`: realm, client, roles, permissions, users (auto-imported)
- `src/main/resources/application.yml`: `issuer-uri`, `keycloak.client-id`
- `security/SecurityConfig.java`, `security/KeycloakJwtConverter.java`
- `controllers/AuthController.java`, `service/AuthService.java`, `dto/LoginRequestDTO.java`, `dto/LoginResponseDTO.java`: login endpoint
- `controllers/DemoSeguridadController.java` (new), plus `@PreAuthorize` on `Duenno`, `Perro` and `Chat` controllers
- `http/seguridad.http`: ready-made requests
