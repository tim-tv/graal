#!/usr/bin/env bash

(cd user-service ; heroku deploy:jar -j build/libs/user-service-0.1.0.jar -i Procfile --app graal-user)
