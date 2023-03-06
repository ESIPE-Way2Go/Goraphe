<template>
  <div class="container-fluid">
    <div class="row no-gutter">
      <div class="col-md-6 d-none d-md-flex bg-image"></div>
      <div class="col-md-6">
        <div class="login d-flex align-items-center py-5">
          <div class="container">
            <div class="row">
              <div class="col-lg-10 col-xl-7 mx-auto">
                <h3 class="display-4">GoRaphe</h3>
                <p class="text-muted mb-4">La route du succès.</p>
                <form @submit.prevent="handleLogin">
                  <div class="mb-3">
                    <input id="inputEmail" v-model="form.username" autofocus=""
                           class="form-control rounded-pill border-0 shadow-sm px-4" placeholder="Nom d'utilisateur"
                           required="" type="text"/>
                  </div>
                  <div class="mb-3">
                    <input id="inputPassword" v-model="form.password"
                           class="form-control rounded-pill border-0 shadow-sm px-4 text-primary"
                           placeholder="Mot de passe"
                           required=""
                           type="password"/>
                  </div>
                  <div class="text-center">
                    <v-btn color="blue" variant="text" @click="this.$router.push({ name: 'forgetPassword'})">
                      Mot de passe oublié ?
                    </v-btn>
                  </div>
                  <div class="d-grid gap-2 mt-2">
                    <button class="btn btn-primary btn-block text-uppercase mb-2 rounded-pill shadow-sm">Se connecter
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {useToast} from "vue-toastification";

export default {
  name: 'LoginPage',
  setup() {
    const toast = useToast();
    return {toast}
  },
  data() {
    return {
      form: {username: "", password: ""},
      errors: {}
    }
  },
  methods: {
    // Connects the user if the username and password are correct, then redirects him to the homepage. Otherwise, shows an error
    handleLogin() {
      this.$store.dispatch("auth/login", this.form).then((response) => {
            if (response.email) {
              this.$router.push({name: 'home'});
              return;
            }
            this.toast.error(response.message)
          },
      );
    },
  }
}
</script>

<style>
.login {
  min-height: 100vh;
}

.bg-image {
  background-image: url('@/assets/roads_backgroung_img.jpg');
  background-size: cover;
  background-position: center center;
}
</style>