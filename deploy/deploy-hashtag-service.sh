#!/usr/bin/env bash

heroku deploy:jar hashtag-service/build/libs/hashtag-service-0.1.0.jar \
        --spring.profiles.active=cloud --app graal-hashtag
