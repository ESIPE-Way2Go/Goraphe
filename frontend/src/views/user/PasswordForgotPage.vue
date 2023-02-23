<template>
  <div class="d-flex align-center fill-height">
    <v-card max-width="50%" class="mx-auto pa-6 bg-white" style="border-color: blue" variant="outlined" rounded-lg>
      <v-img class="align-end text-white" :src=imageGoraphe cover></v-img>
      <v-card-title class="text-center" style="color: blue">
        Mot de passe oublié
      </v-card-title>
      <v-card-text>
        <v-form fast-fail @submit.prevent="sendForgotPassword" v-model="isFormValid">
          <v-text-field v-model="email" :rules="emailRules" label="Email" required></v-text-field>
          <v-btn color="blue" type="submit" class="mt-2" :disabled="!isFormValid">Récupérer le mot de passe</v-btn>
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
        return 'mail est requis'
      },
      value => {
        if (/^\w+([\\.-]?\w+)*@\w+([\\.-]?\w+)*(\.\w{2,})+$/.test(value)) return true
        return 'Mail doit être valide'
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