<template>
  <div class="d-flex align-center fill-height bg-image">
    <v-card class="mx-auto pa-6 bg-white rounded-xl" max-height="700px" style="border-color: blue" variant="outlined"
            width="500px">
      <v-img :src=imageGoraphe class="mx-auto" cover width="50%"></v-img>
      <v-card-title class="text-center ma-5 text-blue-accent-3 font-weight-bold" max>
        Création de compte
      </v-card-title>
      <v-card-text>
        <v-form v-model="isFormValid" fast-fail @submit.prevent="createAccount">
          <v-text-field v-model="form.mail" class="ma-2" label="Mail" readonly variant="outlined"></v-text-field>
          <v-text-field v-model="form.username" :rules="usernameRules" class="ma-2" label="Pseudonyme"
                        variant="outlined"></v-text-field>
          <v-text-field v-model="form.password" :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'" :rules="passwordRules"
                        :type="show1 ? 'text' : 'password'" class="ma-2"
                        hint="Au moins 8 caractères, une majuscule, une minuscule et un chifffre"
                        label="Mot de passe " variant="outlined"
                        @click:append="show1 = !show1"></v-text-field>
          <v-text-field v-model="confirmPassword" :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
                        :rules="confirmPasswordRules"
                        :type="show2 ? 'text' : 'password'" class="ma-2"
                        label="Confirmation du mot de passe" variant="outlined"
                        @click:append="show2 = !show2"></v-text-field>
          <v-btn :disabled="!isFormValid" class="mt-2 rounded-3 bg-blue-darken-1 mx-auto d-table" size="large" type="submit">
            Valider
          </v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
import {useToast} from "vue-toastification";

export default {
  setup() {
    const toast = useToast();
    return {toast}
  },
  data() {
    return {
      mail: '',
      show1: false,
      show2: false,
      usernameRules: [
        value => {
          if (value !== '') return true
          return 'Ce champ ne doit pas être vide'
        },
      ],
      passwordRules: [
        value => {
          if ((/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d).{8,}$/).test(value)) return true
          return 'Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule et un chiffre'
        },
      ],
      confirmPassword: '',
      confirmPasswordRules: [
        value => {
          if (value === this.form.password) return true
          return 'Ce champ n\'est pas identique au champ "Mot de passe"'
        },
      ],

      form: {email: "", username: "", password: ""},
      isFormValid: false,
      imageGoraphe: require('@/assets/Goraphe_small.png'),
    }
  },
  computed: {
    formIsValid() {
      return this.$refs.form.validate()
    },
  },
  methods: {
    checkAutorization() {
      let token = this.$route.params.token
      fetch("/api/administration/checkAccount/" + token, {
        method: "GET",
      }).then(response => response.json()).then(
          data => {
            let email = data['email']
            if (email === undefined)
              this.$router.push({name: 'login'})
            this.form.email = email
            this.mail = email
          }
      );
    },
    createAccount() {
      fetch("/api/administration/createAccount", {
        method: 'PUT',
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(this.form)
      })
          .then(response => {
            if (response.ok)
              this.$router.push({name: 'login'})
            response.json().then(data => {
              this.toast.error(data['message'])
            })
          })
    },
  },
  mounted() {
    this.checkAutorization()
  }
}
</script>

<style>
.bg-image {
  background-image: url('@/assets/roads_backgroung_img.svg');
  background-size: cover;
  background-position: center center;
}
</style>