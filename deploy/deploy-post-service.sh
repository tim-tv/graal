#!/usr/bin/env bash

heroku deploy:jar post-service/build/libs/post-service-0.1.0.jar \
        --spring.profiles.active=cloud --app graal-post
