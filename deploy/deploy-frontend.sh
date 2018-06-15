#!/usr/bin/env bash

git remote add heroku https://heroku:$HEROKU_API_KEY@git.heroku.com/graal-frontend.git
git push heroku `git subtree split --prefix graal-web-app/dist master`:master --force
