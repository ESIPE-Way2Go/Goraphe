<template>
  <div class="d-flex align-center fill-height bg-image">
    <v-card class="mx-auto pa-6 bg-white rounded-xl outline" max-height="750px" style="border-color: blue" variant="outlined"
            width="500px">
      <v-img :src=imageGoraphe class="mx-auto" cover width="50%"></v-img>
      <v-card-title class="text-center ma-5 text-blue-accent-3 font-weight-bold" max>
        Mot de passe oublié
      </v-card-title>
      <v-card-text>
        <v-form v-model="isFormValid" fast-fail @submit.prevent="sendForgotPassword">
          <v-text-field v-model="email" :rules="emailRules" label="Mail" required variant="outlined"></v-text-field>
          <div class="d-flex justify-content-between">
            <v-btn class="mt-2 rounded-3 bg-grey-darken-3" size="large"
                   @click="this.$router.push({ name: 'login'})">
              Retour
            </v-btn>
            <v-btn :disabled="!isFormValid" class="mt-2 rounded-3 bg-blue-darken-1" size="large"
                   type="submit">
              Envoyer
            </v-btn>
          </div>
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
  data: () => ({
    valid: false,
    email: '',
    emailRules: [
      value => {
        if (value) return true
        return 'Un mail est requis'
      },
      value => {
        if (/^\w+([\\.-]?\w+)*@\w+([\\.-]?\w+)*(\.\w{2,})+$/.test(value)) return true
        return 'Le mail doit être valide'
      },
    ],
    isFormValid: false,
    imageGoraphe: require('@/assets/Goraphe_small.png'),
  }),
  computed: {
    formIsValid() {
      return this.$refs.form.validate()
    },
  },
  methods: {
    sendForgotPassword() {
      fetch("/api/administration/forgetPassword/" + this.email, {
        method: "GET",
        headers: {"Content-Type": "application/json"},
      })
          .then(response => {
            if (response.ok) {
              this.toast.success("Mail envoyé")
              this.$router.push({name: 'login'})
            }
            response.json().then(data => {
              this.toast.error(data['message'])
            })
          })
    },
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