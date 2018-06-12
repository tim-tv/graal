#!/usr/bin/env bash

heroku deploy:jar auth-service/build/libs/auth-service-0.1.0.jar \
        --spring.profiles.active=cloud --app graal-auth
