#!/bin/bash

docker-compose -f docker-compose.yml up -d
sbt -jvm-debug 9005 run