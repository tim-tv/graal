#!/usr/bin/env bash

heroku git:remote --app graal-frontend
git subtree push --prefix graal-web-app/dist heroku master
git remote add heroku https://heroku:$HEROKU_API_KEY@git.heroku.com/graal-frontend.git

git push -f heroku master
