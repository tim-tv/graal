const cookie = require('js-cookie')
const axios = require('axios')

const api = require('@/api/api').API

function putUserInfoToCookies (successCallback) {
  api.get('/uaa/me', {
    headers: {
      'Authorization': 'Bearer ' + getAccessToken()
    }
  })
  .then(response => {
    cookie.set('username', response.data.principal.username)
    cookie.set('user_id', response.data.principal.id)

    let isAdmin = response.data.authorities.map(x => x.authority).includes('ROLE_ADMIN')
    cookie.set('is_admin', isAdmin)
    console.log('SUKA ' + cookie.get('user_id'))
    successCallback()
  })
}

function getAccessToken () {
  return cookie.get('access_token')
}

module.exports = {
  login: function (username, password, successCallback, errorCallback) {
    let path = '/uaa/oauth/token'
    let config = {
      auth: {
        username: 'ui-client',
        password: 'ui-client-secret'
      },
      params: {
        grant_type: 'password',
        redirect_uri: 'https://www.yandex.ru',
        username: username,
        password: password
      }
    }

    api.post(path, {}, config)
    .then(response => {
      var resp = response.data
      var expirationTime = new Date(new Date().getTime() + resp.expires_in * 1000)
      var cookieConfig = { expires: expirationTime }

      cookie.set('access_token', response.data.access_token, cookieConfig)
      cookie.set('refresh_token', response.data.access_token, { secure: true })
      axios.defaults.headers.common['Authorization'] = 'Bearer ' + response.data.access_token
      putUserInfoToCookies(successCallback)
    })
    .catch(error => {
      errorCallback(error)
    })

    return true
  },

  register: function (username, email, password, successCallback, errorCallback) {
    let path = '/uaa/register'

    let body = {
      'username': username,
      'email': email,
      'password': password
    }

    api.post(path, body)
    .then(response => {
      successCallback()
    })
    .catch(error => {
      errorCallback(error)
    })

    return true
  },

  logout: function () {
    cookie.remove('access_token')
    cookie.remove('refresh_token')
    cookie.remove('username')
    cookie.remove('user_id')
    cookie.remove('is_admin')
  },

  getCurrentUser: function () {
    return {
      id: cookie.get('user_id'),
      username: cookie.get('username')
    }
  },

  isAuthenticated: function () {
    return cookie.get('access_token') != null
  },

  isAdmin: function () {
    return cookie.get('is_admin')
  },

  setCookie: cookie.set,
  getCookie: cookie.get

}
