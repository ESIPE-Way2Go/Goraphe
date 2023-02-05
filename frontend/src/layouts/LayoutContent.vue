<template>
  <v-app :theme="myCustomLightTheme">

    <v-app-bar>


      <v-app-bar-title class="font-weight-bold">Banxxy</v-app-bar-title>

      <v-spacer></v-spacer>

      <CustomerCreationModal v-if="can('advisor')"></CustomerCreationModal>

        <v-col cols = auto>
          <v-row dense class="font-weight-bold text-lg-subtitle-1 text-sm-subtitle-3"> {{user.username}} </v-row>
          <v-row dense class="text-grey-darken-1 text-caption">{{showUserRole(user.role)}}</v-row>
        </v-col>
        <v-btn
            :prepend-icon="theme.global.name.value === 'myCustomLightTheme' ? 'mdi-weather-sunny' : 'mdi-weather-night'"
            @click="toggleTheme"
        ></v-btn>

        <v-btn icon="mdi-logout" @click="logOut"></v-btn>

    </v-app-bar>

    <v-main>
      <slot></slot>
    </v-main>
  </v-app>
</template>


<script>
import { useTheme } from 'vuetify'
import {can} from '@/utils'
import CustomerCreationModal from "@/components/CustomerCreationModal";
export default {

  components: {CustomerCreationModal},
  setup() {
    const theme = useTheme()
    return {
      theme,
      toggleTheme: () => theme.global.name.value = theme.global.current.value.dark ? 'myCustomLightTheme' : 'dark'
    }
  },
  data () {
    return {
      user: {username: null, role: null},
    }
  },
  methods : {
    can,
    logOut() {
      this.$store.dispatch('auth/logout');
      this.$router.push('/login');
    },
    showUserRole(role){
      switch (role) {
        case "ROLE_ADVISOR" :
          return "Conseiller"
        default :
          return "Client"
      }
    }
  },
  beforeMount() {
    if(this.$store.state.auth.user!==null) {
      this.$data.user.username = this.$store.state.auth.user.username;
      this.$data.user.role = this.$store.state.auth.user.roles[0];
    }
  }
}



</script>

<style scoped>

</style>