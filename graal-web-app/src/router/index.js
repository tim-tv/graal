import Vue from 'vue'
import Router from 'vue-router'
import UserPostsPage from '@/pages/UserPostsPage'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/users/:id',
      name: 'users',
      component: UserPostsPage
    }
  ]
})
