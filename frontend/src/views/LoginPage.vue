<template>
  <div class="container-fluid">
    <div class="row no-gutter">

      <div class="col-md-6 d-none d-md-flex bg-image"></div>

      <div class="col-md-6">
        <div class="login d-flex align-items-center py-5">


          <div class="container">
            <div class="row">
              <div class="col-lg-10 col-xl-7 mx-auto">
                <h3 class="display-4">Banxxy</h3>
                <p class="text-muted mb-4">Reactive Bank application for your safe transaction.</p>
                <form @submit.prevent="handleLogin">
                  <div class="mb-3">
                    <input id="inputEmail" type="text" v-model="form.username" placeholder="username" required="" autofocus="" class="form-control rounded-pill border-0 shadow-sm px-4" />
                  </div>
                  <div class="mb-3">
                    <input id="inputPassword" type="password" v-model="form.password" placeholder="Password" required="" class="form-control rounded-pill border-0 shadow-sm px-4 text-primary" />
                  </div>

                  <div class="d-grid gap-2 mt-2">
                    <button  class="btn btn-primary btn-block text-uppercase mb-2 rounded-pill shadow-sm" >Sign in</button>
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
import { useToast } from "vue-toastification";

export default {
  name: 'LoginPage',
  setup(){
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

    handleLogin(){
        this.$store.dispatch("auth/login", this.form).then((response) => {
              if(response.email) {
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
.login,
.image {
  min-height: 100vh;
}
.bg-image {
  background-image: url('@/assets/login.jpg');
  background-size: cover;
  background-position: center center;
}
</style>