#!/usr/bin/env bash

(cd hashtag-service ; heroku deploy:jar -j build/libs/hashtag-service-0.1.0.jar -i Procfile --app graal-hashtag)
