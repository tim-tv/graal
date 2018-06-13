#!/usr/bin/env bash

(cd aggregation-service ; heroku deploy:jar -j build/libs/aggregation-service-0.1.0.jar -i Procfile --app graal-aggregation)
