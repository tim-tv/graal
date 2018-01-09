<template>
  <v-toolbar class="navbar" fixed color="white">
    <v-spacer></v-spacer>

    <v-toolbar-title class="navbar-title">Graal</v-toolbar-title>
    <v-spacer></v-spacer>
    <v-btn icon @click.stop="dialog = true">
      <v-icon color="grey">create</v-icon>
    </v-btn>

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

    <v-dialog v-model="dialog" persistent max-width="500px">
      <v-card>

        <v-card-title>
          <h1 class="headline grey--text">Create post</h1>
          <v-spacer></v-spacer>
          <v-btn icon @click.native="dialog = false">
            <v-icon color="grey">close</v-icon>
          </v-btn>
        </v-card-title>

        <v-card-text>
          <v-form v-model="valid" @submit.prevent="create" ref="form">
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
            flat @click="createPost"
            color="primary"
            type="submit"
            :disabled="!valid"
          >Create</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-toolbar>
</template>

<style>
@import url('https://fonts.googleapis.com/css?family=Fredoka+One');

.navbar {
  background-color: white;
}

.navbar-title {
  color: #a9a9a9;
  font-family: 'Fredoka One', cursive;
  font-size: 30px;
}
</style>

<script>
import {API} from '@/api/api'

export default {

  data () {
    const defaultForm = Object.freeze({
      caption: '',
      content: '',
      tags: []
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
      message: 'New post has been created',
      defaultForm
    }
  },

  methods: {
    resetForm () {
      this.form = Object.assign({}, this.defaultForm)
      this.$regs.form.reset()
    },

    removeTag (item) {
      this.form.tags.splice(this.form.tags.indexOf(item), 1)
      this.form.tags = [...this.form.tags]
    },

    createPost () {
      this.dialog = false

      let path = '/users/1/posts'
      API.post(path, {
        caption: this.form.caption,
        content: this.form.content,
        tags: this.form.tags
      })
      .then(response => {
        this.successSnackbar = true
      })
      .catch(error => {
        this.message = 'Post has not been created: ' + error.response.data.message
        this.errorSnackbar = true
      })

      this.resetForm()
    }
  }
}
</script>
