#!/usr/bin/env bash

(cd gateway-service ; heroku deploy:jar -j build/libs/gateway-service-0.1.0.jar -i Procfile --app graal-gateway)
