#!/bin/bash
set -euo pipefail

: "${RESOURCE_GROUP:?}" "${LOCATION:?}" "${ACR_NAME:?}" "${ACI_NAME:?}" "${DNS_LABEL:?}"
: "${DB_NAME:?}" "${DB_USER:?}" "${DB_PASSWORD:?}" "${BUILD_ID:?}" "${CONTEXT_DIR:?}"

echo "##[section]Resource Group e ACR"
az group create --name "$RESOURCE_GROUP" --location "$LOCATION" --output none

if ! az acr show --name "$ACR_NAME" --output none 2>/dev/null; then
  az acr create --resource-group "$RESOURCE_GROUP" --name "$ACR_NAME" --sku Basic --admin-enabled true --output none
fi
az acr update --name "$ACR_NAME" --admin-enabled true --output none

cd "$CONTEXT_DIR"

echo "##[section]Build das imagens no ACR"
az acr build --registry "$ACR_NAME" --image "vetflow-api:$BUILD_ID" --image "vetflow-api:latest" --file Dockerfile .
az acr build --registry "$ACR_NAME" --image "vetflow-db:latest" --file Dockerfile.postgres .

ACR_SERVER=$(az acr show --name "$ACR_NAME" --query loginServer --output tsv)
ACR_USER=$(az acr credential show --name "$ACR_NAME" --query username --output tsv)
ACR_PASS=$(az acr credential show --name "$ACR_NAME" --query "passwords[0].value" --output tsv)
echo "##vso[task.setsecret]$ACR_PASS"

YAML_FILE=$(mktemp)
trap 'rm -f "$YAML_FILE"' EXIT

cat > "$YAML_FILE" << YAML
apiVersion: '2021-10-01'
location: $LOCATION
name: $ACI_NAME
properties:
  osType: Linux
  restartPolicy: Always
  ipAddress:
    type: Public
    dnsNameLabel: $DNS_LABEL
    ports:
    - protocol: tcp
      port: 8080
  containers:
  - name: vetflow-db
    properties:
      image: $ACR_SERVER/vetflow-db:latest
      resources:
        requests:
          cpu: 1
          memoryInGb: 1.5
      ports:
      - port: 5432
      environmentVariables:
      - name: POSTGRES_DB
        value: $DB_NAME
      - name: POSTGRES_USER
        value: $DB_USER
      - name: POSTGRES_PASSWORD
        secureValue: '$DB_PASSWORD'
  - name: vetflow-app
    properties:
      image: $ACR_SERVER/vetflow-api:$BUILD_ID
      resources:
        requests:
          cpu: 1
          memoryInGb: 1.5
      ports:
      - port: 8080
      environmentVariables:
      - name: SPRING_DATASOURCE_URL
        value: jdbc:postgresql://localhost:5432/$DB_NAME
      - name: SPRING_DATASOURCE_USERNAME
        value: $DB_USER
      - name: SPRING_DATASOURCE_PASSWORD
        secureValue: '$DB_PASSWORD'
  imageRegistryCredentials:
  - server: $ACR_SERVER
    username: $ACR_USER
    password: '$ACR_PASS'
tags:
  project: vetflow-sprint4
  build: '$BUILD_ID'
type: Microsoft.ContainerInstance/containerGroups
YAML

echo "##[section]Deploy no ACI"
az container delete --resource-group "$RESOURCE_GROUP" --name "$ACI_NAME" --yes --output none 2>/dev/null || true
az container create --resource-group "$RESOURCE_GROUP" --file "$YAML_FILE" --output none

FQDN=$(az container show --resource-group "$RESOURCE_GROUP" --name "$ACI_NAME" --query ipAddress.fqdn --output tsv)
echo "Aplicacao: http://$FQDN:8080/login"

echo "##[section]Aguardando a aplicacao responder"
CODE=000
for i in $(seq 1 40); do
  CODE=$(curl -s -o /dev/null -w "%{http_code}" "http://$FQDN:8080/login" || true)
  if [ "$CODE" = "200" ]; then
    break
  fi
  echo "tentativa $i: HTTP $CODE"
  sleep 15
done

if [ "$CODE" != "200" ]; then
  echo "##vso[task.logissue type=error]Aplicacao nao respondeu apos o deploy"
  exit 1
fi

echo "Deploy concluido: http://$FQDN:8080/login"
