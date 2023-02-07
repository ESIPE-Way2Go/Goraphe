<template>
  <div>
    <h3>Banxyy</h3>
    <form @submit.prevent="handleLogin">
      <input id="email" type="mail" v-model="form.username" />
      <input id="inputPassword" type="password" v-model="form.password" />

      <div class="d-grid gap-2 mt-2">
        <button  class="btn btn-primary btn-block text-uppercase mb-2 rounded-pill shadow-sm" >Sign in</button>
      </div>
    </form>
  </div>

</template>

<script>
import { useToast } from "vue-toastification"
  export default {
    name: 'LoginPage',
    setup() {
      const toast = useToast();
      return { toast }
    },
    data() {
      return {
        form: {username: "", password: ""},
        errors: {}
      }
    },
    methods: {
      handleLogin() {
        this.$store.dispatch("auth/login", this.form).then((response) => {
          if (response.email) {
            this.$router.push({name: 'home'})
            return;
          }
          this.toast.error(response.message)
        },
        );
      }
    }
  }
</script>