#!/usr/bin/env bash

sh deploy/deploy-eureka-service.sh
sh deploy/deploy-auth-service.sh
sh deploy/deploy-user-service.sh
sh deploy/deploy-hashtag-service.sh
sh deploy/deploy-post-service.sh
sh deploy/deploy-aggregation-service.sh
sh deploy/deploy-gateway-service.sh
sh deploy/deploy-statistic-service.sh
sh deploy/deploy-frontend.sh
