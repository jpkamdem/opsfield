#!/bin/bash
BUILDTOOL=$1

while [ "$BUILDTOOL" != "p" ] && [ "$BUILDTOOL" != "d" ]; do
  echo "podman, docker"
  read BUILDTOOL
done

if [ "$BUILDTOOL" == "p" ]; then
  podman compose down
fi

if [ "$BUILDTOOL" == "d" ]; then
  docker compose down
fi

exit 1
