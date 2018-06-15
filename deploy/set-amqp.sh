APP=$1

heroku config:set CLOUD_AMQP_HOST=baboon.rmq.cloudamqp.com --app ${APP}

heroku config:set CLOUD_AMQP_VHOST=sbidwipj --app ${APP}

heroku config:set CLOUD_AMQP_USERNAME=sbidwipj --app ${APP}

heroku config:set CLOUD_AMQP_PASSWORD=MTNj5xbPZAafTIYeeRKUKYMwiTQ4NE5s --app ${APP}
