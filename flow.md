# EDC Authentication & Federated Catalog Flow

## Architecture Diagram

```mermaid
sequenceDiagram
    participant K as Keycloak
    participant C1 as Company1<br/>(Provider)
    participant C2 as Company2<br/>(Consumer + FC)
    participant C3 as Company3<br/>(Provider)
    participant DB as PostgreSQL<br/>(FC Cache)

    Note over K: Keycloak Realm: dataspace
    Note over K: Groups:<br/>- EDC-Company1 (US)<br/>- EDC-Company2 (DE)<br/>- EDC-Company3 (US)

    rect rgb(230, 240, 255)
        Note left of K: 1. JWT Token Generation
        C2->>K: Request token<br/>(client_id: edc-consumer)
        K->>K: Authenticate with<br/>client-jwt certificate
        K->>K: Add claims from group:<br/>country=DE<br/>participant_id=company2
        K-->>C2: JWT Token with claims
        Note right of C2: Token contains:<br/>- country: DE<br/>- participant_id: company2<br/>- aud: keycloak:8080/realms/dataspace
    end

    rect rgb(255, 240, 230)
        Note left of C2: 2. Federated Catalog Discovery
        C2->>C2: Load nodes.properties:<br/>company1=http://company1:19194/protocol<br/>company3=http://company3:19194/protocol
        
        C2->>C1: POST /protocol/catalog<br/>(with JWT token)
        C1->>C1: Validate JWT via Keycloak JWKS
        C1->>C1: Apply catalog policy<br/>(check country claim)
        C1-->>C2: Return catalog entries
        
        C2->>C3: POST /protocol/catalog<br/>(with JWT token)
        C3->>C3: Validate JWT via Keycloak JWKS
        C3->>C3: Policy evaluation:<br/>Required: country=DE<br/>Token has: country=DE ✅
        C3-->>C2: Return catalog entries
    end

    rect rgb(240, 255, 240)
        Note left of DB: 3. Federated Catalog Storage
        C2->>DB: Store catalogs in<br/>federated_catalog_consumer schema
        Note right of DB: Periodic crawling<br/>every 120 seconds
    end
```

## Component Details

```mermaid
graph TB
    subgraph Keycloak["🔐 Keycloak Configuration"]
        KC1[OAuth Clients]
        KC2[Service Accounts]
        KC3[Groups with Attributes]
        KC4[Protocol Mappers]
        
        KC1 --> |edc-provider| SA1[service-account-edc-provider]
        KC1 --> |edc-consumer| SA2[service-account-edc-consumer]
        KC1 --> |edc-company3| SA3[service-account-edc-company3]
        
        SA1 --> G1[EDC-Company1<br/>country: US]
        SA2 --> G2[EDC-Company2<br/>country: DE]
        SA3 --> G3[EDC-Company3<br/>country: US]
        
        KC4 --> |Maps to JWT| CLAIMS[JWT Claims:<br/>- participant_id<br/>- country<br/>- audience]
    end

    subgraph EDC2["🔄 Company2 (Federated Catalog)"]
        FC[Federated Catalog<br/>Crawler]
        NF[nodes.properties]
        CACHE[PostgreSQL<br/>FC Cache]
        
        NF --> |Lists nodes| FC
        FC --> |Crawls periodically| NODES[Company1<br/>Company3]
        FC --> |Stores catalogs| CACHE
    end

    subgraph Policy["📋 Catalog Policy (Company3)"]
        P1[Germany-only Policy]
        P2[Policy Engine]
        P3[JWT Validation]
        
        P3 --> |Extracts claims| P2
        P1 --> |country = DE| P2
        P2 --> |✅ PASSED| ALLOW[Allow Catalog Access]
    end
```

## Configuration Flow

```mermaid
graph LR
    subgraph Setup["Initial Setup"]
        V[vault.properties<br/>Private/Public Keys]
        N[nodes.properties<br/>Node URLs]
        E[Environment Variables]
    end

    subgraph Runtime["Runtime Configuration"]
        E --> |EDC_OAUTH_CLIENT_ID| CLIENT[OAuth Client]
        E --> |EDC_OAUTH_TOKEN_URL| TOKEN[Token Endpoint]
        E --> |EDC_OAUTH_PROVIDER_JWKS_URL| JWKS[JWKS for Validation]
        V --> |Keys| SIGN[JWT Signing]
        N --> |Node List| CRAWL[FC Crawler]
    end

    subgraph DataFlow["Data Flow"]
        CLIENT --> JWT[JWT Token]
        JWT --> |Contains claims| REQ[Catalog Request]
        REQ --> |Policy check| RESP[Catalog Response]
        RESP --> |Cache| DB[(PostgreSQL)]
    end
```

## Key Configuration Points

### 1. **Keycloak Setup**
- Each EDC connector has its own OAuth client
- Service accounts linked to groups with country attributes
- JWT tokens include participant_id and country claims

### 2. **Federated Catalog (Company2)**
```yaml
EDC_CATALOG_CACHE_ENABLED: true
EDC_CATALOG_CACHE_EXECUTION_PERIOD_SECONDS: 120
EDC_NODES_FILE_PATH: /resources/nodes.properties
```

### 3. **Policy Evaluation (Company3)**
- Checks country claim in JWT token
- Required: country = DE
- Token has: country = DE (from Company2's group)
- Result: ✅ POLICY CHECK PASSED

### 4. **Database Schemas**
- `provider` - Company1 data
- `consumer` - Company2 data  
- `company3` - Company3 data
- `federated_catalog_consumer` - FC cache for Company2

## Authentication Flow Summary

1. **Token Request**: Company2 requests JWT from Keycloak using client credentials
2. **Token Generation**: Keycloak generates JWT with claims from group attributes
3. **Catalog Request**: Company2 uses JWT to request catalogs from other nodes
4. **Policy Validation**: Target nodes validate JWT and apply policies
5. **Catalog Response**: If policies pass, catalog data is returned
6. **Cache Storage**: Company2 stores catalogs in PostgreSQL for federated access