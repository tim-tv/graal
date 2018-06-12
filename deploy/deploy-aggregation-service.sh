#!/usr/bin/env bash

heroku deploy:jar aggregation-service/build/libs/aggregation-service-0.1.0.jar \
        --spring.profiles.active=cloud --app graal-aggregation

