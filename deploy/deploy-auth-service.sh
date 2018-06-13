#!/usr/bin/env bash

(cd auth-service ; heroku deploy:jar -j build/libs/auth-service-0.1.0.jar -i Procfile --app graal-auth)
