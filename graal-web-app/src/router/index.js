import Vue from 'vue'
import Router from 'vue-router'
import UserPostsPage from '@/pages/UserPostsPage'
import AdminPage from '@/pages/AdminPage'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/users/:id',
      name: 'users',
      component: UserPostsPage
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminPage
    }
  ]
})
