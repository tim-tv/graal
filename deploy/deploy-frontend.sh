#!/usr/bin/env bash

heroku git:remote --app graal-frontend
git subtree push --prefix graal-web-app/dist heroku master
