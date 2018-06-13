#!/usr/bin/env bash

(cd statistic-service ; heroku deploy:jar -j build/libs/statistic-service-0.1.0.jar -i Procfile --app graal-statistic)
