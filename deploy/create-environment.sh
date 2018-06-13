#!/usr/bin/env bash

EUREKA_URL=https://graal-eureka.herokuapp.com
heroku create --app graal-eureka

sh deploy/create-registered-service.sh graal-hashtag ${EUREKA_URL}

sh deploy/create-registered-service.sh graal-user ${EUREKA_URL}

sh deploy/create-registered-service.sh graal-post ${EUREKA_URL}

sh deploy/create-registered-service.sh graal-auth ${EUREKA_URL}

sh deploy/create-registered-service.sh graal-aggregation ${EUREKA_URL}
heroku addons:create cloudamqp --app graal-aggregation

sh deploy/create-registered-service.sh graal-statistic ${EUREKA_URL}

