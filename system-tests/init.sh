#!/bin/bash

# Wait for connectors to be healthy
echo "Waiting for connectors to be ready..."
sleep 30

# Company1 (Provider) setup
echo "Setting up Company1 (Provider)..."

# Create asset
curl -X POST http://company1:19193/management/v3/assets \
  -H "Content-Type: application/json" \
  -d '{
    "@context":  [
      "https://w3id.org/edc/connector/management/v0.0.1"
      ],
    "@id": "asset-1",
    "properties": {
      "name": "Product Data",
      "description": "Product catalog and inventory data",
      "version": "1.0.0",
      "contenttype": "application/json"
    },
    "dataAddress": {
      "@type": "DataAddress",
      "type": "HttpData",
      "baseUrl": "https://jsonplaceholder.typicode.com/users",
      "proxyPath": "true",
      "proxyQueryParams": "true"
    }
  }'

# Create policy
# curl -X POST http://company1:19193/management/v3/policydefinitions \
#   -H "Content-Type: application/json" \
#   -d '{
#     "@context": {
#       "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
#       "odrl": "http://www.w3.org/ns/odrl/2/"
#     },
#     "@id": "policy-1",
#     "policy": {
#       "@type": "odrl:Set",
#       "odrl:permission": [{
#         "odrl:action": "USE",
#         "odrl:constraint": {
#           "@type": "LogicalConstraint",
#           "odrl:or": [{
#             "@type": "Constraint",
#             "odrl:leftOperand": "BusinessPartnerNumber",
#             "odrl:operator": {
#               "@id": "odrl:eq"
#             },
#             "odrl:rightOperand": "BPNL00000001"
#           }]
#         }
#       }]
#     }
#   }'


# curl -X POST http://company1:19193/management/v3/policydefinitions \
#   -H "Content-Type: application/json" \
#   -d '{
#     "@id": "policy-1",
#     "privateProperties": {
#       "name": "Policy",
#       "https://w3id.org/edc/v0.0.1/ns/tenantId": "default"
#     },
#     "policy": {
#       "@type": "odrl:Set"
#     },
#     "@context": {
#       "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
#       "edc": "https://w3id.org/edc/v0.0.1/ns/",
#       "odrl": "http://www.w3.org/ns/odrl/2/"
#     }
#   }'


# Create policy definition

# curl -X POST http://company1:19193/management/v3/contractdefinitions \
#   -H "Content-Type: application/json" \
#   -d '{
#     "@context": { "@vocab": "https://w3id.org/edc/v0.0.1/ns/" },
#     "@id": "contract-1",
#     "accessPolicyId": "policy-1",
#     "contractPolicyId": "policy-1",
#     "assetsSelector": [{
#       "@type": "CriterionDto",
#       "operandLeft": "https://w3id.org/edc/v0.0.1/ns/id",
#       "operator": "=",
#       "operandRight": "asset-1"
#     }]
#   }'


echo "\nCreating Germany-only policy..."
curl -X POST "http://company1:19193/management/v3/policydefinitions" \
  -H "Content-Type: application/json" \
  -d '{
    "@context":  [
      "https://w3id.org/edc/connector/management/v0.0.1"
      ],
    "@type": "PolicyDefinition",
    "@id": "germany-only-policy",
    "policy": {
      "@type": "Set",
      "permission": {
        "action": "use",
        "constraint": [
        {
          "leftOperand": "country",
          "operator": {
            "@id": "odrl:eq"
          },
          "rightOperand": "DE"
        },
        {
          "leftOperand": "participantId",
          "operator": {
            "@id": "odrl:isPartOf"
          },
          "rightOperand": "company2"
        }
        ]
      }
    }
  }'

# Create contract definition
echo "\nCreating contract definition..."
curl -X POST "http://company1:19193/management/v3/contractdefinitions" \
  -H "Content-Type: application/json" \
  -d '{
    "@context":  [
      "https://w3id.org/edc/connector/management/v0.0.1"
      ],
    "@type": "ContractDefinition",
    "@id": "contract-1",
    "accessPolicyId": "germany-only-policy",
    "contractPolicyId": "germany-only-policy",
    "assetsSelector": [
      {
        "@type": "CriterionDto",
        "operandLeft": "id",
        "operator": "=",
        "operandRight": "asset-1"
      }
    ]
  }'


# Company3 (Provider) setup
echo "Setting up Company3 (Provider)..."

# Create a sample asset for company3
curl -X POST http://company3:19193/management/v3/assets \
  -H "Content-Type: application/json" \
  -d '{
    "@context":  [
      "https://w3id.org/edc/connector/management/v0.0.1"
      ],
    "@id": "asset-3",
    "properties": {
      "name": "Product Data",
      "description": "Product catalog and inventory data",
      "version": "1.0.0",
      "contenttype": "application/json"
    },
    "dataAddress": {
      "@type": "DataAddress",
      "type": "HttpData",
      "baseUrl": "https://jsonplaceholder.typicode.com/users",
      "proxyPath": "true",
      "proxyQueryParams": "true"
    }
  }'

# Create a sample policy for company3
echo "\nCreating Germany-only policy..."
curl -X POST "http://company3:19193/management/v3/policydefinitions" \
  -H "Content-Type: application/json" \
  -d '{
    "@context":  [
      "https://w3id.org/edc/connector/management/v0.0.1"
      ],
    "@type": "PolicyDefinition",
    "@id": "germany-only-policy3",
    "policy": {
      "@type": "Set",
      "permission": {
        "action": "use",
        "constraint": [
        {
          "leftOperand": "country",
          "operator": {
            "@id": "odrl:eq"
          },
          "rightOperand": "DE"
        },
        {
          "leftOperand": "participantId",
          "operator": {
            "@id": "odrl:isPartOf"
          },
          "rightOperand": "company2"
        }
        ]
      }
    }
  }'

# Create contract definition for company3
echo "\nCreating contract definition..."
curl -X POST "http://company3:19193/management/v3/contractdefinitions" \
  -H "Content-Type: application/json" \
  -d '{
    "@context":  [
      "https://w3id.org/edc/connector/management/v0.0.1"
      ],
    "@type": "ContractDefinition",
    "@id": "contract-3",
    "accessPolicyId": "germany-only-policy3",
    "contractPolicyId": "germany-only-policy3",
    "assetsSelector": [
      {
        "@type": "CriterionDto",
        "operandLeft": "id",
        "operator": "=",
        "operandRight": "asset-3"
      }
    ]
  }'


echo "Initialization complete!"