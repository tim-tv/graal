import Vue from 'vue'
import Vuetify from 'vuetify'
import 'js-cookie'

import App from './App'
import router from './router'

import 'vuetify/dist/vuetify.min.css'

Vue.use(Vuetify)

Vue.config.productionTip = false

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: { App }
})
