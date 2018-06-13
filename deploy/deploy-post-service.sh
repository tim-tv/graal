#!/usr/bin/env bash

(cd post-service ; heroku deploy:jar -j build/libs/post-service-0.1.0.jar -i Procfile --app graal-post)
