<template>
  <v-app :theme="myCustomLightTheme">
    <v-app-bar density="compact" absolute color="transparent" flat class="pt-2">

      <template v-slot:prepend>
        <v-btn v-if="can('user')" class="mx-2" @click="home">
          <v-img :src="Goraphe" cover width="40" class="bg-surface rounded-circle"></v-img>
        </v-btn>
        <v-col v-else cols="1">
          <v-img :src="Goraphe" class="mx-3 bg-surface rounded-circle" width="40"  ></v-img>
        </v-col>
      </template>
      <template v-slot:append v-if="!mobile">
        <v-spacer></v-spacer>
        <v-btn v-if="can('user')" class="text-capitalize mx-2 rounded-pill" color="primary" variant="flat" @click="newSimulation">
          <v-icon class="mr-2" icon="mdi-plus-circle"></v-icon>
          Nouvelle simulation
        </v-btn>
        <v-btn v-if="can('admin')" class="text-capitalize mx-2 rounded-pill" color="surface" variant="flat" @click="administration">
          <v-icon class="mr-2" icon="mdi-account-group"></v-icon>
          Administration
        </v-btn>

        <v-btn v-if="can('user')" class="mx-2 rounded-circle" icon="mdi-logout-variant" @click="logOut" color="surface" variant="flat"></v-btn>
      </template>

      <template v-slot:append v-else>
          <v-divider></v-divider>
        <v-btn v-if="can('user')" class="mx-2 rounded-circle" icon="mdi-menu" @click.stop="drawer = !drawer" color="surface" variant="flat"></v-btn>
        </template>

    </v-app-bar>

    <v-navigation-drawer v-if="mobile"
        v-model="drawer"
        location="top"
        disable-resize-watcher
        class="displayer"
        sticky>
      <v-list class="pt-15" density="comfortable" variant="flat" nav>
        <v-list-item v-if="can('user')" prepend-icon="mdi-plus-circle" title="Nouvelle simulation" @click="newSimulation"></v-list-item>
        <v-list-item v-if="can('admin')" prepend-icon="mdi-account-group" title="Administration" @click="administration"></v-list-item>
        <v-list-item v-if="can('user')" prepend-icon="mdi-logout-variant" title="Déconnexion" @click="logOut"></v-list-item>
      </v-list>



    </v-navigation-drawer>


    <v-main class="main">
      <slot></slot>
    </v-main>
  </v-app>
</template>

<script>
import {useDisplay, useTheme} from 'vuetify'
import {can} from '@/utils'
import Goraphe from '@/assets/Goraphe_small_no_text_no_background.png';



export default {
  setup() {
    const theme = useTheme()
    const {mobile} = useDisplay()




    return {
      theme,
      toggleTheme: () => theme.global.name.value = theme.global.current.value.dark ? 'myCustomLightTheme' : 'dark',
      mobile,
    }

  },
  data() {
    return {
      user: {username: null, role: null},
      Goraphe: Goraphe, // The image logo to be used in the navbar
      drawer: false,
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
</script>

<style>

.main{
  padding: 0 !important;
}

.displayer{
  top: 0 !important;
}

</style>