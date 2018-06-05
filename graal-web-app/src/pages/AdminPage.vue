<template>
  <v-container fluid grid-list-md class="grey lighten-4">
    <v-layout row wrap>
      <v-btn color="info" @click="drawTopTags(5)">Show top 5 hashtags</v-btn>
      <v-btn color="info" @click="drawTopTags(20)">Show top 20 hashtags</v-btn>
      <v-btn color="info" @click="drawTopTags(50)">Show top 50 hashtags</v-btn>
    </v-layout>

    <div v-if='isTopTagsDrawed'>
      <bar-chart v-bind:dataset="topTagsData"></bar-chart>
    </div>

    <v-flex>
      <v-card flat>
        <v-spacer></v-spacer>
          <span class="grey--text display-2">{{ errorMessage }}</span>
        <v-spacer></v-spacer>
        </v-card>
    </v-flex>
  </v-container>
</template>

<script>
import { API } from '@/api/api'
import BarChart from '@/components/BarChart'

const cookie = require('js-cookie')
const axios = require('axios')

export default {

  components: {
    BarChart
  },

  data () {
    return {
      isTopTagsDrawed: false,
      currentPlot: 'bar-chart',
      errorMessage: '',
      topTagsData: {}
    }
  },

  methods: {

    drawTopTags (n) {
      this.isTopTagsDrawed = true
      this.fetchTopUsedTags(n)
    },

    fetchTopUsedTags (n) {
      let path = '/stats/tags/top?size=' + n

      axios.defaults.headers.common['Authorization'] = 'Bearer ' + cookie.get('access_token')

      API.get(path)
      .then(response => {
        let data = response.data.content
        let values = data.map(item => item.count)
        let labels = data.map(item => item.tag)

        this.topTagsData = {
          'values': values,
          'labels': labels,
          'title': 'Top ' + n + ' tags'
        }
      })
      .catch(e => {
        this.errorMessage = 'Ooops...\n Something went wrong. Please, try later.'
      })
    }
  }

}
</script>
