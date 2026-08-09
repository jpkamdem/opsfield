#!/bin/bash
buildah rm --all
podman system prune -af
docker system prune -af