#!/usr/bin/env bash

EUREKA_URL=https://graal-eureka.herokuapp.com

sh deploy/create-registered-service.sh graal-hashtag ${EUREKA_URL}
