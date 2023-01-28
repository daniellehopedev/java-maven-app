#! /usr/bin/env bash

# setting image env variable on the server
export IMAGE=$1
docker-compose -f docker-compose.yaml up --detach
echo "success"