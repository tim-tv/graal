<template>
  <v-app>
  <div id="app">
    <app-nav></app-nav>
    <router-view class="app-content"></router-view>
  </div>
  </v-app>
</template>

<script>
import AppNav from '@/components/AppNav'

export default {
  components: {
    AppNav
  },

  name: 'app'
}
</script>

<style>
@import url('https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Material+Icons');

.app-content {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;

  margin-top: 60px;
}
</style>
