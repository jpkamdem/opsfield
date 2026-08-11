#!/bin/bash
podman compose up -d database-manager
mvn clean spring-boot:run