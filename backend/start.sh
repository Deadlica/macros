#!/usr/bin/env bash

set -euo pipefail
cd "$(dirname "$0")"

set -a
source ../deploy/.env
set +a

export DB_PASSWORD="$POSTGRES_PASSWORD"
./mvnw spring-boot:run
