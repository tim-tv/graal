<template>
  <v-flex d-flex xs12 md6 lg4>
    <v-card color="white">
      <v-card-title>
        <div id="username" class=" teal--text lighten-3">@{{ post.user.nickName }}</div>
        <div id="post-created-date" class="grey--text lighten-1 body-1">{{ fromNow(post.createdDateTime) }}</div>

        <v-menu bottom right open-on-hover>
          <v-btn class="menu-btn" icon dark slot="activator">
            <v-icon color="grey">more_vert</v-icon>
          </v-btn>
          <v-list>
            <v-list-tile v-if="isCurrentUser()" class="body-1" @click="" @click.native="dialog = true">modify</v-list-tile>
          </v-list>
        </v-menu>
      </v-card-title>

      <v-divider class="post-divider grey lighten-1"></v-divider>

      <v-card-text class="text-xs-center">
        <span id="open-quote" class="post-quote teal--text lighten-1">“</span>
          <span id="post-content" v-model="postContent">{{ post.content }}</span>
        <span id="close-quote" class="post-quote teal--text lighten-1">”</span>
      </v-card-text>

      <v-divider class="post-divider grey lighten-1"></v-divider>

      <div class="tag-container">
        <span v-for="tag in post.tags">
          <span class="tag light-blue--text accent-3"> #{{ tag }} </span>
        </span>
      </div>

    <v-dialog v-model="dialog" persistent max-width="500px">
      <v-card>

        <v-card-title>
          <h1 class="headline grey--text">Modify post</h1>
          <v-spacer></v-spacer>
          <v-btn icon @click.native="dialog = false">
            <v-icon color="grey">close</v-icon>
          </v-btn>
        </v-card-title>

        <v-card-text>
          <v-form v-model="valid" @submit.prevent="modify" ref="form">
            <v-container grid-list-md>
              <v-layout wrap>
                <v-flex xs12>
                  <v-text-field
                    multi-line
                    label="Caption"
                    color="teal lighten-1"
                    :counter="200"
                    :rows="3"
                    :rules="captionRules"
                    v-model="form.caption"
                    ></v-text-field>
                </v-flex>

                <v-flex xs12>
                  <v-text-field
                    multi-line
                    label="Text"
                    color="teal lighten-1"
                    :counter="500"
                    :rows="4"
                    :rules="contentRules"
                    v-model="form.content"
                    ></v-text-field>
                </v-flex>

                <v-select
                  label="Tags"
                  hint="Press enter to add a new tag"
                  color="teal lighten-1"
                  chips
                  tags
                  append-icon=""
                  clearable
                  v-model="form.tags"
                >
                  <template slot="selection" slot-scope="data">
                    <v-chip
                      label
                      close
                      @input="removeTag(data.item)"
                      :selected="data.selected"
                    >
                      {{ data.item }}
                    </v-chip>
                  </template>
                </v-select>

              </v-layout>
            </v-container>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" flat @click.native="dialog = false">Close</v-btn>
          <v-btn
            flat @click="modifyPost"
            color="primary"
            type="submit"
            :disabled="!valid"
          >Apply</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-snackbar
      v-model="successSnackbar"
      bottom
      right
      color="teal"
    >
      <span>{{ message }}</span>
      <v-icon dark>check_circle</v-icon>
    </v-snackbar>

    <v-snackbar
      v-model="errorSnackbar"
      bottom
      right
      color="red"
    >
      <span>{{ message }}</span>
      <v-icon dark>error</v-icon>
    </v-snackbar>

    </v-card>
  </v-flex>
</template>

<script>
import moment from 'moment'

import {API} from '@/api/api'

const auth = require('@/api/auth')

export default {
  props: ['post'],

  data () {
    const defaultForm = Object.freeze({
      caption: this.post.caption,
      content: this.post.content,
      tags: this.post.tags
    })

    return {
      dialog: false,
      form: Object.assign({}, defaultForm),
      valid: true,
      contentRules: [
        (v) => !!v || 'Post content is required',
        (v) => (v && v.length <= 500) || 'Content must be no more than 500 characters'
      ],
      captionRules: [
        (v) => (v && v.length <= 200) || 'Caption must be no more than 200 characters'
      ],
      errorSnackbar: false,
      successSnackbar: false,
      message: 'New post has been updated',
      postContent: '',
      defaultForm
    }
  },

  methods: {

    isAuthenticated () {
      return auth.isAuthenticated()
    },

    isCurrentUser () {
      return auth.isAuthenticated() && (auth.getCurrentUser().id === this.$route.params.id)
    },

    fromNow: function calcTimeFromNow (timestamp) {
      return moment(timestamp).fromNow()
    },

    resetForm () {
      this.form = Object.assign({}, this.defaultForm)
      this.$regs.form.reset()
    },

    removeTag (item) {
      this.form.tags.splice(this.form.tags.indexOf(item), 1)
      this.form.tags = [...this.form.tags]
    },

    modifyPost () {
      this.dialog = false

      let path = '/users/' + this.$route.params.id + '/posts/' + this.post.id
      API.put(path, {
        caption: this.form.caption,
        content: this.form.content,
        tags: this.form.tags
      })
      .then(response => {
        this.post.content = this.form.content
        this.post.caption = this.form.caption
        this.post.tags = this.form.tags
        this.successSnackbar = true
      })
      .catch(error => {
        this.message = 'Post has not been updated: ' + error.response.data.message
        this.errorSnackbar = true
      })

      this.resetForm()
    }
  }

}
</script>

<style>
@import url('https://fonts.googleapis.com/css?family=Roboto+Slab');

.menu-btn {
  margin-bottom: 0px;
  margin-top: 0px;
  margin-right: 0px;
}

.post-divider {
  width: 80%;
  margin: auto;
}

#username {
  font-size: 20px;
  color: blue;
}

#post-created-date {
  margin-left: auto;
  margin-right: 0;
  font-size: 16px;
}

.post-quote {
  padding-top: 20px;
  font-size: 28px;
}

#post-content {
  margin: 10px;

  font-size: 18px;
  font-family: 'Roboto Slab', serif;
  color: #3a3a3a;
}

.tag-container {
  width: 100%;
  margin: 15px;
}
</style>
