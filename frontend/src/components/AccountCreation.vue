<template>

  <div class="d-flex flex-column justify-center align-center h-100">

    <h3 class="display-6 text-grey" > Creation d'un compte pour le client {{user.firstName}} {{user.lastName}} </h3>
<!--    <p class="text-muted mb-4">Le compte serra vide</p>-->
  <v-card elevation="3" max-width="1000" class="w-100 p-2">
  <v-form
      ref="form"
      v-model="valid"
      @submit.prevent="handleCreation"
  >

    <v-container>
      <v-row>
        <v-col cols="12">
          <v-text-field
              variant="solo"
              v-model="account.title"
              :rules="titleRules"
              label="Name"
              required
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
  name: "AccountCreation",
  props:{
    user: {
      type: Object,
      default: () =>({ userId: null ,firstName: "",lastName: ""})
    }
  },
  setup(){
    const toast = useToast();
    return {
      toast,
    }
  },
  data: () => ({
    valid:true,
    account:{
      title:'',
      id_owner:'',
      id_advisor:'',
    },

    titleRules:[
        v => Boolean(v) || "Title is required",
        v => v.length <= 20 || "Name must be less than 20 characters"
    ],
  }),
  methods:{
     async handleCreation(){
       if(this.$refs.form.validate()){
         const account = this.$data.account
         account.id_advisor = this.$store.state.auth.user.id;
         account.id_owner = this.user.userId;
         //await CreateCustomer.create(myCustomer)
         const API_URL = '/api/account/create';
        try {
          const response =  await fetch(`${API_URL}`,{
            method: 'POST',
            headers: authHeader(),
            body: JSON.stringify(account)
          });
          const result = await response.json();
          if (result!==null){
            this.toast.success("Création du compte réussi");
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