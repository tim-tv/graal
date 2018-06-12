#!/usr/bin/env bash

APP_NAME=$1
EUREKA_URL=$2

heroku create --app ${APP_NAME}
heroku config:set EUREKA_URL=${EUREKA_URL} --app ${APP_NAME}
heroku config:set DOMAIN_NAME="${APP_NAME}.herokuapp.com" --app ${APP_NAME}
