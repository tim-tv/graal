<template>
  <v-navigation-drawer v-model="navDrawer" disable-resize-watcher absolute light class="white" app>

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
          <v-list-tile-title>sign in</v-list-tile-title>
        </v-list-tile-content>
      </v-list-tile>

      <!-- register tile -->
      <v-list-tile v-if="!isAuthenticated()" @click="showRegisterDialog">
        <v-list-tile-action>
          <v-icon>account_circle</v-icon>
        </v-list-tile-action>
        <v-list-tile-content>
          <v-list-tile-title>sign up</v-list-tile-title>
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

      <!-- admin tile -->
      <v-list-tile v-if="isAuthenticated() && isAdmin()" @click="open_admin_panel">
        <v-list-tile-action>
          <v-icon>device_hub</v-icon>
        </v-list-tile-action>
        <v-list-tile-content>
          <v-list-tile-title>admin panel</v-list-tile-title>
        </v-list-tile-content>
      </v-list-tile>
    </v-list>

    <v-dialog v-model="registerDialog" persistent max-width="500px">
      <v-card>

        <v-card-title>
          <h1 class="headline grey--text">Sign Up</h1>
          <v-spacer></v-spacer>
          <v-btn icon @click.native="registerDialog = false">
            <v-icon color="grey">close</v-icon>
          </v-btn>
        </v-card-title>

        <v-card-text>
          <v-form v-model="valid" @submit.prevent="register" ref="registerForm">
            <v-container grid-list-md>
              <v-layout wrap>
                <v-flex xs12>
                  <v-text-field
                    label="Username"
                    color="teal lighten-1"
                    :rules="usernameRules"
                    v-model="registerForm.username"
                    ></v-text-field>
                </v-flex>

                <v-flex xs12>
                  <v-text-field
                    label="Email"
                    color="teal lighten-1"
                    :rules="emailRules"
                    v-model="registerForm.email"
                    ></v-text-field>
                </v-flex>

                <v-flex xs12>
                  <v-text-field
                    label="Password"
                    color="teal lighten-1"
                    :append-icon="passwordIcon ? 'visibility' : 'visibility_off'"
                    :append-icon-cb='changePasswordIconState'
                    :type="passwordIcon ? 'password' : 'text'"
                    :rules="passwordRules"
                    v-model="registerForm.password"
                    ></v-text-field>
                </v-flex>

              </v-layout>
            </v-container>
          </v-form>

          <div class="red--text"> {{ registerForm.errorMessage }} </div>

        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" flat @click.native="registerDialog = false">Close</v-btn>
          <v-btn
            flat @click="register"
            color="primary"
            type="submit"
            :disabled="!valid"
          >Log in</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>


    <v-dialog v-model="authDialog" persistent max-width="500px">
      <v-card>

        <v-card-title>
          <h1 class="headline grey--text">Sing In</h1>
          <v-spacer></v-spacer>
          <v-btn icon @click.native="authDialog = false">
            <v-icon color="grey">close</v-icon>
          </v-btn>
        </v-card-title>

        <v-card-text>
          <v-form v-model="valid" @submit.prevent="authorize" ref="loginForm">
            <v-container grid-list-md>
              <v-layout wrap>
                <v-flex xs12>
                  <v-text-field
                    label="Username"
                    color="teal lighten-1"
                    :rules="usernameRules"
                    v-model="loginForm.username"
                    ></v-text-field>
                </v-flex>

                <v-flex xs12>
                  <v-text-field
                    :type="'password'"
                    label="Password"
                    color="teal lighten-1"
                    :rules="passwordRules"
                    v-model="loginForm.password"
                    ></v-text-field>
                </v-flex>

              </v-layout>
            </v-container>
          </v-form>

          <div class="red--text"> {{ loginForm.errorMessage }} </div>

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

    const defaultRegisterForm = Object.freeze({
      username: '',
      email: '',
      password: '',
      errorMessage: ''
    })

    return {
      passwordIcon: false,
      navDrawer: false,
      authDialog: false,
      registerDialog: false,
      loginForm: Object.assign({}, defaultForm),
      registerForm: Object.assign({}, defaultRegisterForm),
      valid: true,
      usernameRules: [
        (v) => !!v || 'Post content is required'
      ],
      passwordRules: [
        (v) => !!v || 'Post content is required'
      ],
      emailRules: [
        v => {
          return !!v || 'E-mail is required'
        },
        v => /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || 'E-mail must be valid'
      ],
      errorSnackbar: false,
      successSnackbar: false,
      message: 'New post has been created',
      defaultForm,
      defaultRegisterForm
    }
  },

  methods: {

    changePasswordIconState () {
      this.passwordIcon = !this.passwordIcon
    },

    currentUser () {
      return auth.getCurrentUser().username
    },

    isAuthenticated () {
      return auth.isAuthenticated()
    },

    isAdmin () {
      var flag = null
      if (auth.isAdmin()) {
        flag = 'ok'
      }
      return flag
    },

    showDrawer () {
      this.navDrawer = true
    },

    login () {
      this.authDialog = true
      this.navDrawer = false
    },

    showRegisterDialog () {
      this.registerDialog = true
      this.navDrawer = false
    },

    logout () {
      auth.logout()
      this.navDrawer = false
      this.$forceUpdate()
    },

    open_admin_panel () {
      console.log('cat: ' + auth.getCurrentUser().id)
      this.$router.push('admin')
    },

    resetForm () {
      this.loginForm = Object.assign({}, this.defaultForm)
      this.$regs.loginForm.reset()
    },

    register () {
      var errorHandler = msg => {
        this.registerForm.errorMessage = 'Registration error: ' + msg
      }

      var successHandler = () => {
        this.registerDialog = false
        this.resetForm()
        this.$forceUpdate()
      }
      auth.register(this.registerForm.username, this.registerForm.email, this.registerForm.password, successHandler, errorHandler)
    },

    authorize () {
      var errorHandler = _ => {
        this.loginForm.errorMessage = 'Authorization error. Check your username and password and try again.'
      }

      var successHandler = () => {
        this.$router.push({
          name: 'users',
          params: {
            id: auth.getCurrentUser().id
          }
        })
        this.authDialog = false
        this.resetForm()
        this.$forceUpdate()
      }
      auth.login(this.loginForm.username, this.loginForm.password, successHandler, errorHandler)
    }
  }
}
</script>
