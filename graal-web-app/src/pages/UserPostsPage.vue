<template>
  <v-container fluid grid-list-md class="grey lighten-4">
    <v-layout row wrap>
      <post-card v-for="post in posts"
                 v-bind:post="post"
                 v-bind:key="post.id"
                 prop-name="post">
      </post-card>

      <v-flex>
        <v-card flat>
          <v-spacer></v-spacer>
            <span class="grey--text display-2">{{ errorMessage }}</span>
          <v-spacer></v-spacer>
          </v-card>
      </v-flex>

      <v-flex xs12 v-if="currentElements < totalElements">
        <v-btn @click="loadPage">Load more</v-btn>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import {API} from '@/api/api'
import PostCard from '@/components/post/PostCard'

const cookie = require('js-cookie')
const axios = require('axios')

export default {
  components: {
    PostCard
  },

  data () {
    return {
      posts: [],
      page: 0,
      currentElements: 0,
      totalElements: 1,
      errorMessage: ''
    }
  },

  methods: {
    loadPage () {
      let id = parseInt(this.$route.params.id)
      let path = '/users/' + id + '/posts?size=9&page=' + this.page

      API.get(path)
      .then(response => {
        this.posts = this.posts.concat(response.data.content)
        this.page += 1
      })
      .catch(e => {
        console.log(e)
        this.errorMessage = 'Ooops...\n Something went wrong. Please, try later.'
      })
    }
  },

  created () {
    let id = parseInt(this.$route.params.id)
    let path = '/users/' + id + '/posts?size=9'

    axios.defaults.headers.common['Authorization'] = 'Bearer ' + cookie.get('access_token')

    API.get(path)
    .then(response => {
      this.posts = response.data.content
      this.currentElements = response.numberOfElements
      this.totalElements = response.totalElements
    })
    .catch(e => {
      this.errorMessage = 'Ooops...\n Something went wrong. Please, try later.'
    })
  }
}
</script>
