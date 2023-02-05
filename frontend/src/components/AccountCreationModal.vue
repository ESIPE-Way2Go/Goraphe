<template>
  <v-btn
      variant="flat"
      color="primary"
      rounded="lg"
      @click="dialogCreatAccount=true"
      class="mr-2">
      Créer un compte
  </v-btn>

  <v-dialog
      v-model="dialogCreatAccount"
      fullscreen
  >
    <v-card >
      <v-toolbar
          dark
          color="primary"
      >
        <v-btn
            icon
            dark
            @click="dialogCreatAccount = false"
        >
          <v-icon>mdi-close</v-icon>
        </v-btn>
        <v-toolbar-title></v-toolbar-title>
        <v-spacer></v-spacer>
      </v-toolbar>

      <AccountCreation :user="user" @close="close"></AccountCreation>

    </v-card>
  </v-dialog>
</template>

<script>
import AccountCreation from "@/components/AccountCreation";
export default {
  name: "AccountCreationModal",
  props:{
    user: {
      type: Object,
      default: () =>({ userId: null,firstName: "",lastName: ""})
    }
  },
  components: {AccountCreation},
  setup(){
    return{

    }
  },
  data() {
    return{
      dialogCreatAccount: false,
    }
  },
  methods:{
    close(){
      this.dialogCreatAccount= false;
      this.$emit("resetComptes");
    }
  }
}
</script>

<style scoped>

</style>