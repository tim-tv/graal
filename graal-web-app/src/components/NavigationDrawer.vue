<template>
  <v-navigation-drawer v-model="navDrawer" enable-resize-watcher absolute light class="white" app>

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


    <v-toolbar flat>
      <v-list>
        <v-list-tile>
          <v-list-tile-title class="title grey--text">
            <div v-if="isAuthenticated()"> @{{ currentUser() }} </div>
            <div v-else>Graal</div>
          </v-list-tile-title>
        </v-list-tile>
      </v-list>
    </v-toolbar>
    <v-divider></v-divider>
    <v-list>
      <!-- login tile -->
      <v-list-tile v-if="!isAuthenticated()" @click="login">
        <v-list-tile-action>
          <v-icon>account_circle</v-icon>
        </v-list-tile-action>
        <v-list-tile-content>
          <v-list-tile-title>login</v-list-tile-title>
        </v-list-tile-content>
      </v-list-tile>

      <!-- logout tile -->
      <v-list-tile v-if="isAuthenticated()" @click="logout">
        <v-list-tile-action>
          <v-icon>exit_to_app</v-icon>
        </v-list-tile-action>
        <v-list-tile-content>
          <v-list-tile-title>logout</v-list-tile-title>
        </v-list-tile-content>
      </v-list-tile>

    </v-list>


    <v-dialog v-model="authDialog" persistent max-width="500px">
      <v-card>

        <v-card-title>
          <h1 class="headline grey--text">Authorize</h1>
          <v-spacer></v-spacer>
          <v-btn icon @click.native="authDialog = false">
            <v-icon color="grey">close</v-icon>
          </v-btn>
        </v-card-title>

        <v-card-text>
          <v-form v-model="valid" @submit.prevent="authorize" ref="form">
            <v-container grid-list-md>
              <v-layout wrap>
                <v-flex xs12>
                  <v-text-field
                    label="Username"
                    color="teal lighten-1"
                    :rules="usernameRules"
                    v-model="form.username"
                    ></v-text-field>
                </v-flex>

                <v-flex xs12>
                  <v-text-field
                    label="Password"
                    color="teal lighten-1"
                    :rules="passwordRules"
                    v-model="form.password"
                    ></v-text-field>
                </v-flex>

              </v-layout>
            </v-container>
          </v-form>

          <div class="red--text"> {{ form.errorMessage }} </div>

        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" flat @click.native="authDialog = false">Close</v-btn>
          <v-btn
            flat @click="authorize"
            color="primary"
            type="submit"
            :disabled="!valid"
          >Log in</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-navigation-drawer>
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
const auth = require('@/api/auth')

export default {
  props: ['drawer'],

  data () {
    const defaultForm = Object.freeze({
      username: '',
      password: '',
      errorMessage: ''
    })

    return {
      navDrawer: false,
      authDialog: false,
      form: Object.assign({}, defaultForm),
      valid: true,
      usernameRules: [
        (v) => !!v || 'Post content is required'
      ],
      passwordRules: [
        (v) => !!v || 'Post content is required'
      ],
      errorSnackbar: false,
      successSnackbar: false,
      message: 'New post has been created',
      defaultForm
    }
  },

  methods: {

    currentUser () {
      return auth.getCurrentUser().username
    },

    isAuthenticated () {
      return auth.isAuthenticated()
    },

    showDrawer () {
      this.navDrawer = true
    },

    login () {
      this.authDialog = true
      this.navDrawer = false
    },

    logout () {
      auth.logout()
      this.navDrawer = false
    },

    resetForm () {
      this.form = Object.assign({}, this.defaultForm)
      this.$regs.form.reset()
    },

    authorize () {
      var errorHandler = error => {
        this.form.errorMessage = 'Authorization error. Check your username and password and try again.'
      }

      var successHandler = () => {
        this.authDialog = false
        this.resetForm()
      }
      auth.login(this.form.username, this.form.password, successHandler, errorHandler)
    }
  }
}
</script>
