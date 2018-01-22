<template>
  <v-app>
    <div id="app">
      <navigation-drawer ref="navigationDrawer" app></navigation-drawer>
      <app-nav v-on:openDrawer="openDrawer"></app-nav>
      <router-view class="app-content"></router-view>
    </div>
  </v-app>
</template>

<script>
import AppNav from '@/components/AppNav'
import NavigationDrawer from '@/components/NavigationDrawer'

export default {
  data () {
    return {
      drawer: true
    }
  },

  methods: {
    openDrawer: function () {
      this.drawer = true
      this.$refs.navigationDrawer.showDrawer()
    }
  },

  components: {
    AppNav,
    NavigationDrawer
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
