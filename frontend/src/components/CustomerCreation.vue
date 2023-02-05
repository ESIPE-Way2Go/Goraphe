<template>

  <div class="d-flex flex-column justify-center align-center h-100">

    <h3 class="display-5 text-grey" > Creation d'un client</h3>
    <p class="text-muted mb-4">Un compte par défaut sera ajouté au client </p>
  <v-card elevation="3" max-width="1000" class="w-100 p-2">
  <v-form
      ref="form"
      v-model="valid"
      @submit.prevent="handleCreation"
  >

    <v-container>
      <v-row>
        <v-col cols ="12" md="6">
          <v-text-field
              variant="solo"
            v-model="customer.firstName"
            :rules="nameRules"
            :counter="20"
            label="First name"
            required>
          </v-text-field>
        </v-col>
        <v-col cols ="12" md="6">
          <v-text-field
              variant="solo"
              v-model="customer.lastName"
              :rules="nameRules"
              :counter="20"
              label="Last name"
              required
              >
          </v-text-field>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12">
          <v-text-field
              variant="solo"
              v-model="customer.mail"
              :rules="emailRules"
              label="E-mail"
              required
          ></v-text-field>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12" >
          <v-text-field
              variant="solo"
              v-model="customer.userName"
              :rules="usernameRules"
              label="User name"
              required
          ></v-text-field>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12" >
          <v-text-field
              variant="solo"
              :append-icon="showPwd ? 'mdi-eye' : 'mdi-eye-off'"
              v-model="customer.password"
              :rules="passwordRules"
              :type="showPwd ? 'text' : 'password'"
              label="Password"
              required
              @click:append="showPwd = !showPwd"
          ></v-text-field>
        </v-col>
      </v-row>

      <v-row>
        <v-btn
            type="submit"
            class="ml-4 mr-4 "
            :disabled="!valid"
        >
          submit
        </v-btn>
        <v-btn @click="clear">
          clear
        </v-btn>
      </v-row>

    </v-container>

  </v-form>
  </v-card>
  </div>
</template>

<script>
//import CreateCustomer from "@/services/customer.create";
import authHeader from "@/services/auth-header";
import { useToast } from "vue-toastification";


export default {
  name: "CustomerCreation",
  setup(){
    const toast = useToast();
    return {
      toast,
    }
  },
  data: () => ({
    valid:true,
    customer:{
      firstName:'',
      lastName:'',
      mail:'',
      userName:'',
      password:'',
      advisorId:'',
    },

    nameRules:[
        v => Boolean(v) || "Name is required",
        v => v.length <= 10 || "Name must be less than 10 characters"
    ],
    emailRules:[
      v => Boolean(v) || "Email is required",
      v => /.+@+/.test(v) || "Email must be valid"
    ],
    usernameRules:[
      v => Boolean(v) || "Name is required",
    ],
    passwordRules:[
      v => Boolean(v) || "Password is required",
      v => v.length >= 8 || "Password must be more than 8 characters"
    ],
    showPwd:false
  }),
  methods:{
     async handleCreation(){
       if(this.$refs.form.validate()){
         const myCustomer = this.$data.customer
         myCustomer.advisorId = this.$store.state.auth.user.id;
         //await CreateCustomer.create(myCustomer)
         const API_URL = '/api/user/customer/create';
        try {
          const response =  await fetch(`${API_URL}`,{
            method: 'POST',
            headers: authHeader(),
            body: JSON.stringify(myCustomer)
          });
          const result = await response.json();

          if (result!==null){
            this.toast.success("Création du client réussi");
            //this.$router.push("dashboard");
            this.$emit("close");
          }
        }catch(error){
          this.toast.error(error.message);
        }

       }

    },
    clear(){
      this.$refs.form.reset()
    }
  //  TODO handle invalid of submit
  }
}


</script>

<style scoped>
.userInfo{
  width: 50%;
}

</style>