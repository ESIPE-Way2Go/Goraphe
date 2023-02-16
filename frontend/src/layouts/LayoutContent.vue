<template>
  <v-app :theme="myCustomLightTheme">
    <v-app-bar>

      <v-btn v-if="user.role === 'ROLE_ADMIN' || user.role === 'ROLE_USER'" class="mx-2 rounded-pill" @click="home">
        <v-img :src="Goraphe" cover width="45"></v-img>
      </v-btn>
      <v-col v-else cols="1">
        <v-img :src="Goraphe" class="mx-3" width="45"></v-img>
      </v-col>
      <v-spacer></v-spacer>
      <v-btn v-if="user.role === 'ROLE_ADMIN' || user.role === 'ROLE_USER'"
             class="text-capitalize mx-2 rounded-pill bg-blue-lighten-1" @click="newSimulation">
        <v-icon class="mr-2" icon="mdi-plus-circle"></v-icon>
        Nouvelle simulation
      </v-btn>
      <v-btn v-if="user.role === 'ROLE_ADMIN'" class="text-capitalize mx-2 rounded-pill"
             @click="administration">
        <v-icon class="mr-2" icon="mdi-account-group"></v-icon>
        Administration
      </v-btn>
      <v-col class="mx-2" cols=auto>
        <v-row class="font-weight-bold v-row--dense justify-center">{{ user.username }}</v-row>
        <v-row class="v-row--dense justify-center">{{ showUserRole() }}</v-row>
      </v-col>
      <v-btn
          :icon="theme.global.name.value === 'myCustomLightTheme' ? 'mdi-weather-sunny' : 'mdi-weather-night'"
          class="mx-2 rounded-circle" @click="toggleTheme"
      ></v-btn>
      <v-btn v-if="user.role === 'ROLE_ADMIN' || user.role === 'ROLE_USER'" class="mx-2 rounded-circle"
             icon="mdi-logout-variant" @click="logOut"></v-btn>

    </v-app-bar>
    <v-main>
      <slot></slot>
    </v-main>
  </v-app>
</template>


<script>
import {useTheme} from 'vuetify'
import {can} from '@/utils'
import Goraphe from '@/assets/Goraphe_small_no_background.png';

export default {
  setup() {
    const theme = useTheme()
    return {
      theme,
      toggleTheme: () => theme.global.name.value = theme.global.current.value.dark ? 'myCustomLightTheme' : 'dark'
    }
  },
  data() {
    return {
      user: {username: null, role: null},
      Goraphe: Goraphe, // The image logo to be used in the navbar
    }
  },
  methods: {
    can,
    home() {
      this.$router.push('/'); // Redirect to the /home route
    },
    newSimulation() {
      this.$router.push('/map'); // Redirect to the /map route (to create a new simulation)
    },
    administration() {
      this.$router.push('/admin'); // Redirect to the /admin route (used by the admins)
    },
    logOut() {
      this.$store.dispatch('auth/logout'); // Disconnect the user
      this.$router.push('/login'); // Redirect to the /login route (so the user can reconnect itself)
    },
    showUserRole(role) {
      switch (role) {
        case "ROLE_ADMIN" :
          return "Administrateur"
        case "ROLE_USER" :
          return "Utilisateur"
        default :
          return "Invité"

      }
    }
  },
  beforeMount() {
    if (this.$store.state.auth.user !== null) {
      this.$data.user.username = this.$store.state.auth.user.username;
      this.$data.user.role = this.$store.state.auth.user.roles[0];
    } else { // When the person seeing the page is not a user of the website
      this.$data.user.username = "anonyme";
      this.$data.user.role = "ROLE_GUEST";
    }
  },
}

